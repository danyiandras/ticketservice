package com.example.ticketservice.controller;

import static com.example.ticketservice.TestConstants.searchEnd;
import static com.example.ticketservice.TestConstants.searchStart;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.format.DateTimeFormatter;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.example.ticketservice.controller.dto.MovieDto;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
class MovieControllerTest {

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private MockMvc mvc;

	@Test
	void testGetById() throws Exception {
		MovieDto movieOne = objectMapper.readValue(mvc.perform(get("/movie/1")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andReturn()
		.getResponse()
		.getContentAsString(), MovieDto.class);
		
		assertEquals(1, movieOne.getId());
	}

	@Test
	void testGetById_400() throws Exception {
		mvc.perform(get("/movie/0")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().is(400));
	}

	@Test
	void testGetById_404() throws Exception {
		mvc.perform(get("/movie/"+Integer.MAX_VALUE)
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().is(404));		
	}

	@Test
	void testGetByTitle() throws Exception {
		MovieDto movieTaxiDriver = objectMapper.readValue(mvc.perform(get("/movie/title/Taxi Driver")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andReturn()
		.getResponse()
		.getContentAsString(), MovieDto.class);
		
		assertEquals("Taxi Driver", movieTaxiDriver.getTitle());
	}

	@Test
	void testGetByTitle_404() throws Exception {
		mvc.perform(get("/movie/title/NoSuchMovie")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().is(404));		
	}

	@Test
	void testGetAllPlayingMovies() throws Exception {
		MovieDto[] moviesPlaying = objectMapper.readValue(mvc.perform(get("/movie")
				.param("startTime", searchStart.format(DateTimeFormatter.ISO_ZONED_DATE_TIME))
				.param("endTime", searchEnd.format(DateTimeFormatter.ISO_ZONED_DATE_TIME))
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andReturn()
		.getResponse()
		.getContentAsString(), MovieDto[].class);
		
		//[MovieDto(id=4, title=Taxi Driver), MovieDto(id=1, title=The Godfather)]
		MovieDto movieTaxiDriver = objectMapper.readValue(mvc.perform(get("/movie/title/Taxi Driver")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andReturn()
		.getResponse()
		.getContentAsString(), MovieDto.class);
		MovieDto movieTheGodfather = objectMapper.readValue(mvc.perform(get("/movie/title/The Godfather")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andReturn()
		.getResponse()
		.getContentAsString(), MovieDto.class);

		assertEquals(List.of(movieTaxiDriver, movieTheGodfather), List.of(moviesPlaying));
	}

	@Test
	void testGetAllPlayingMovies_400() throws Exception {
		mvc.perform(get("/movie")
				.param("startTime", searchEnd.format(DateTimeFormatter.ISO_ZONED_DATE_TIME))
				.param("endTime", searchStart.format(DateTimeFormatter.ISO_ZONED_DATE_TIME))
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().is(400))
		;
	}

}
