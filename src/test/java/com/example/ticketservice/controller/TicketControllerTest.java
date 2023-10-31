package com.example.ticketservice.controller;

import static com.example.ticketservice.TestConstants.searchEnd;
import static com.example.ticketservice.TestConstants.searchStart;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.example.ticketservice.GetAllAvailableSeatsForScreeningMethodConcurrentCreateAspect;
import com.example.ticketservice.controller.dto.MovieDto;
import com.example.ticketservice.controller.dto.ScreeningDto;
import com.example.ticketservice.controller.dto.SeatDto;
import com.example.ticketservice.controller.dto.TheaterDto;
import com.example.ticketservice.controller.dto.TicketDto;
import com.example.ticketservice.controller.dto.TicketOrderDto;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
class TicketControllerTest {

	@Autowired
	GetAllAvailableSeatsForScreeningMethodConcurrentCreateAspect getAllAvailableSeatsMethodAspect;
	
	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private MockMvc mvc;

	@Test
	void createTicket() throws Exception {
		MovieDto movieTheGodfather = objectMapper.readValue(mvc.perform(get("/movie/title/The Godfather")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andReturn()
		.getResponse()
		.getContentAsString(), MovieDto.class);

		TheaterDto theaterOne = objectMapper.readValue(mvc.perform(get("/theater/name/Theater One")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andReturn()
		.getResponse()
		.getContentAsString(), TheaterDto.class);

		ScreeningDto[] screeningsPlaying = objectMapper.readValue(mvc.perform(get("/screening")
				.param("startTime", searchStart.format(DateTimeFormatter.ISO_ZONED_DATE_TIME))
				.param("endTime", searchEnd.format(DateTimeFormatter.ISO_ZONED_DATE_TIME))
				.param("movieId", movieTheGodfather.getId().toString())
				.param("theaterId", theaterOne.getId().toString())
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andReturn()
		.getResponse()
		.getContentAsString(), ScreeningDto[].class);
		
		SeatDto[] seatsAvailable = objectMapper.readValue(mvc.perform(get("/seat")
				.param("screeningId", screeningsPlaying[0].getId().toString())
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andReturn()
		.getResponse()
		.getContentAsString(), SeatDto[].class);

		TicketOrderDto ticketOrderDto = 
				new TicketOrderDto(screeningsPlaying[0].getId(), 
						List.of(seatsAvailable[0].getId(), seatsAvailable[1].getId(), seatsAvailable[2].getId()));
		
		TicketDto[] tickets = objectMapper.readValue(mvc.perform(post("/ticket/book")
				.content(objectMapper.writeValueAsString(ticketOrderDto))
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isCreated())
		.andReturn()
		.getResponse()
		.getContentAsString(), TicketDto[].class);

		assertEquals(3, tickets.length);
		assertEquals(1, Arrays.asList(tickets).stream().filter(t -> t.getSeatId() == seatsAvailable[0].getId()).count());
		assertEquals(1, Arrays.asList(tickets).stream().filter(t -> t.getSeatId() == seatsAvailable[1].getId()).count());
		assertEquals(1, Arrays.asList(tickets).stream().filter(t -> t.getSeatId() == seatsAvailable[2].getId()).count());
		Arrays.asList(tickets).stream().forEach(ticket -> assertEquals(theaterOne.getName(), ticket.getScreeningTheaterName()));
		Arrays.asList(tickets).stream().forEach(ticket -> assertEquals(movieTheGodfather.getTitle(), ticket.getScreeningMovieTitle()));
		Arrays.asList(tickets).stream().forEach(ticket -> assertEquals(screeningsPlaying[0].getId(), ticket.getScreeningId()));
		
	}

	@Test
	void createTicket_conflict() throws Exception {
		MovieDto movieTheGodfather = objectMapper.readValue(mvc.perform(get("/movie/title/The Godfather")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andReturn()
		.getResponse()
		.getContentAsString(), MovieDto.class);

		TheaterDto theaterOne = objectMapper.readValue(mvc.perform(get("/theater/name/Theater One")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andReturn()
		.getResponse()
		.getContentAsString(), TheaterDto.class);

		ScreeningDto[] screeningsPlaying = objectMapper.readValue(mvc.perform(get("/screening")
				.param("startTime", searchStart.format(DateTimeFormatter.ISO_ZONED_DATE_TIME))
				.param("endTime", searchEnd.format(DateTimeFormatter.ISO_ZONED_DATE_TIME))
				.param("movieId", movieTheGodfather.getId().toString())
				.param("theaterId", theaterOne.getId().toString())
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andReturn()
		.getResponse()
		.getContentAsString(), ScreeningDto[].class);
		
		SeatDto[] seatsAvailable = objectMapper.readValue(mvc.perform(get("/seat")
				.param("screeningId", screeningsPlaying[0].getId().toString())
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andReturn()
		.getResponse()
		.getContentAsString(), SeatDto[].class);

		TicketOrderDto ticketOrderDto1 = 
				new TicketOrderDto(screeningsPlaying[0].getId(), 
						List.of(seatsAvailable[0].getId(), seatsAvailable[1].getId(), seatsAvailable[2].getId()));
		
		mvc.perform(post("/ticket/book")
				.content(objectMapper.writeValueAsString(ticketOrderDto1))
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isCreated());

		TicketOrderDto ticketOrderDto2 = 
				new TicketOrderDto(screeningsPlaying[0].getId(), 
						List.of(seatsAvailable[2].getId(), seatsAvailable[3].getId(), seatsAvailable[4].getId()));

		mvc.perform(post("/ticket/book")
				.content(objectMapper.writeValueAsString(ticketOrderDto2))
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isConflict());

	}

	@Test
	void createTicketN() throws Exception {
		MovieDto movieTheGodfather = objectMapper.readValue(mvc.perform(get("/movie/title/The Godfather")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andReturn()
		.getResponse()
		.getContentAsString(), MovieDto.class);

		TheaterDto theaterOne = objectMapper.readValue(mvc.perform(get("/theater/name/Theater One")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andReturn()
		.getResponse()
		.getContentAsString(), TheaterDto.class);

		ScreeningDto[] screeningsPlaying = objectMapper.readValue(mvc.perform(get("/screening")
				.param("startTime", searchStart.format(DateTimeFormatter.ISO_ZONED_DATE_TIME))
				.param("endTime", searchEnd.format(DateTimeFormatter.ISO_ZONED_DATE_TIME))
				.param("movieId", movieTheGodfather.getId().toString())
				.param("theaterId", theaterOne.getId().toString())
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andReturn()
		.getResponse()
		.getContentAsString(), ScreeningDto[].class);
		
		TicketDto[] tickets = objectMapper.readValue(mvc.perform(post("/ticket/bookn")
				.param("screeningId", screeningsPlaying[0].getId().toString())
				.param("numberOfTickets", "3")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isCreated())
		.andReturn()
		.getResponse()
		.getContentAsString(), TicketDto[].class);

		assertEquals(3, tickets.length);
		Arrays.asList(tickets).stream().forEach(ticket -> assertEquals(theaterOne.getName(), ticket.getScreeningTheaterName()));
		Arrays.asList(tickets).stream().forEach(ticket -> assertEquals(movieTheGodfather.getTitle(), ticket.getScreeningMovieTitle()));
		Arrays.asList(tickets).stream().forEach(ticket -> assertEquals(screeningsPlaying[0].getId(), ticket.getScreeningId()));
		
	}

	@Test
	void createTicketN_400() throws Exception {
		MovieDto movieTheGodfather = objectMapper.readValue(mvc.perform(get("/movie/title/The Godfather")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andReturn()
		.getResponse()
		.getContentAsString(), MovieDto.class);

		TheaterDto theaterOne = objectMapper.readValue(mvc.perform(get("/theater/name/Theater One")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andReturn()
		.getResponse()
		.getContentAsString(), TheaterDto.class);

		ScreeningDto[] screeningsPlaying = objectMapper.readValue(mvc.perform(get("/screening")
				.param("startTime", searchStart.format(DateTimeFormatter.ISO_ZONED_DATE_TIME))
				.param("endTime", searchEnd.format(DateTimeFormatter.ISO_ZONED_DATE_TIME))
				.param("movieId", movieTheGodfather.getId().toString())
				.param("theaterId", theaterOne.getId().toString())
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andReturn()
		.getResponse()
		.getContentAsString(), ScreeningDto[].class);
		
		mvc.perform(post("/ticket/bookn")
				.param("screeningId", screeningsPlaying[0].getId().toString())
				.param("numberOfTickets", "21")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().is(400));
		
	}

	@Test
	void createTicketN_404() throws Exception {
		mvc.perform(post("/ticket/bookn")
				.param("screeningId", String.valueOf(Long.MAX_VALUE))
				.param("numberOfTickets", "3")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().is(404));
		
	}

	
	@Test
	void createTicketNRetry_409() throws Exception {
		MovieDto movieTheGodfather = objectMapper.readValue(mvc.perform(get("/movie/title/The Godfather")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andReturn()
		.getResponse()
		.getContentAsString(), MovieDto.class);

		TheaterDto theaterOne = objectMapper.readValue(mvc.perform(get("/theater/name/Theater One")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andReturn()
		.getResponse()
		.getContentAsString(), TheaterDto.class);

		ScreeningDto[] screeningsPlaying = objectMapper.readValue(mvc.perform(get("/screening")
				.param("startTime", searchStart.format(DateTimeFormatter.ISO_ZONED_DATE_TIME))
				.param("endTime", searchEnd.format(DateTimeFormatter.ISO_ZONED_DATE_TIME))
				.param("movieId", movieTheGodfather.getId().toString())
				.param("theaterId", theaterOne.getId().toString())
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andReturn()
		.getResponse()
		.getContentAsString(), ScreeningDto[].class);

		getAllAvailableSeatsMethodAspect.on = true;
		mvc.perform(post("/ticket/booknretry")
				.param("screeningId", screeningsPlaying[0].getId().toString())
				.param("numberOfTickets", "3")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isConflict())
		;
		getAllAvailableSeatsMethodAspect.on = false;
	}

	@Test
	void createTicketNRetry() throws Exception {
		MovieDto movieTheGodfather = objectMapper.readValue(mvc.perform(get("/movie/title/The Godfather")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andReturn()
		.getResponse()
		.getContentAsString(), MovieDto.class);

		TheaterDto theaterOne = objectMapper.readValue(mvc.perform(get("/theater/name/Theater One")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andReturn()
		.getResponse()
		.getContentAsString(), TheaterDto.class);

		ScreeningDto[] screeningsPlaying = objectMapper.readValue(mvc.perform(get("/screening")
				.param("startTime", searchStart.format(DateTimeFormatter.ISO_ZONED_DATE_TIME))
				.param("endTime", searchEnd.format(DateTimeFormatter.ISO_ZONED_DATE_TIME))
				.param("movieId", movieTheGodfather.getId().toString())
				.param("theaterId", theaterOne.getId().toString())
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andReturn()
		.getResponse()
		.getContentAsString(), ScreeningDto[].class);
		
		TicketDto[] tickets = objectMapper.readValue(mvc.perform(post("/ticket/booknretry")
				.param("screeningId", screeningsPlaying[0].getId().toString())
				.param("numberOfTickets", "3")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isCreated())
		.andReturn()
		.getResponse()
		.getContentAsString(), TicketDto[].class);

		assertEquals(3, tickets.length);
		Arrays.asList(tickets).stream().forEach(ticket -> assertEquals(theaterOne.getName(), ticket.getScreeningTheaterName()));
		Arrays.asList(tickets).stream().forEach(ticket -> assertEquals(movieTheGodfather.getTitle(), ticket.getScreeningMovieTitle()));
		Arrays.asList(tickets).stream().forEach(ticket -> assertEquals(screeningsPlaying[0].getId(), ticket.getScreeningId()));
		
	}

	@Test
	void createTicketNRetry_400() throws Exception {
		MovieDto movieTheGodfather = objectMapper.readValue(mvc.perform(get("/movie/title/The Godfather")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andReturn()
		.getResponse()
		.getContentAsString(), MovieDto.class);

		TheaterDto theaterOne = objectMapper.readValue(mvc.perform(get("/theater/name/Theater One")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andReturn()
		.getResponse()
		.getContentAsString(), TheaterDto.class);

		ScreeningDto[] screeningsPlaying = objectMapper.readValue(mvc.perform(get("/screening")
				.param("startTime", searchStart.format(DateTimeFormatter.ISO_ZONED_DATE_TIME))
				.param("endTime", searchEnd.format(DateTimeFormatter.ISO_ZONED_DATE_TIME))
				.param("movieId", movieTheGodfather.getId().toString())
				.param("theaterId", theaterOne.getId().toString())
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andReturn()
		.getResponse()
		.getContentAsString(), ScreeningDto[].class);
		
		mvc.perform(post("/ticket/booknretry")
				.param("screeningId", screeningsPlaying[0].getId().toString())
				.param("numberOfTickets", "21")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().is(400));
		
	}

	@Test
	void createTicketNRetry_404() throws Exception {
		mvc.perform(post("/ticket/booknretry")
				.param("screeningId", String.valueOf(Long.MAX_VALUE))
				.param("numberOfTickets", "3")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().is(404));
		
	}

}
