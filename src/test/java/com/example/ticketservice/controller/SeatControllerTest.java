package com.example.ticketservice.controller;

import static com.example.ticketservice.TestConstants.searchEnd;
import static com.example.ticketservice.TestConstants.searchStart;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.example.ticketservice.controller.dto.MovieDto;
import com.example.ticketservice.controller.dto.ScreeningDto;
import com.example.ticketservice.controller.dto.SeatDto;
import com.example.ticketservice.controller.dto.TheaterDto;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
class SeatControllerTest {

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private MockMvc mvc;

	@Test
	void testGetById() throws Exception {
		SeatDto seatOne = objectMapper.readValue(mvc.perform(get("/seat/1")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andReturn()
		.getResponse()
		.getContentAsString(), SeatDto.class);
		
		assertEquals(1, seatOne.getId());
	}

	@Test
	void testGetById_400() throws Exception {
		mvc.perform(get("/seat/0")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().is(400));
	}

	@Test
	void testGetById_404() throws Exception {
		mvc.perform(get("/seat/"+Integer.MAX_VALUE)
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().is(404));		
	}

	@Test
	void getAllAvailableSeatsForScreening() throws Exception {
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

		assertEquals(20, seatsAvailable.length);
		Arrays.asList(seatsAvailable).stream().forEach(seat -> assertEquals(theaterOne.getId(), seat.getTheaterId()));
	}

	@Test
	void getAllAvailableSeatsForScreening_404() throws Exception {
		mvc.perform(get("/seat")
				.param("screeningId", String.valueOf(Long.MAX_VALUE))
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().is(404));

	}

}
