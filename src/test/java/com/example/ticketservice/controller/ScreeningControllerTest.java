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
import com.example.ticketservice.controller.dto.TheaterDto;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
class ScreeningControllerTest {

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private MockMvc mvc;

	@Test
	void testGetById() throws Exception {
		ScreeningDto screeningOne = objectMapper.readValue(mvc.perform(get("/screening/1")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andReturn()
		.getResponse()
		.getContentAsString(), ScreeningDto.class);
		
		assertEquals(1, screeningOne.getId());
	}

	@Test
	void testGetById_400() throws Exception {
		mvc.perform(get("/screening/0")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().is(400));
	}

	@Test
	void testGetById_404() throws Exception {
		mvc.perform(get("/screening/"+Integer.MAX_VALUE)
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().is(404));		
	}

	@Test
	void testGetAllPlayingScreenings() throws Exception {
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
		
		log.info(Arrays.toString(screeningsPlaying));
		assertEquals(1, screeningsPlaying.length);
		assertEquals("The Godfather", screeningsPlaying[0].getMovieTitle());
		assertEquals("Theater One", screeningsPlaying[0].getTheaterName());
	}

	@Test
	void testGetAllPlayingScreenings_400() throws Exception {
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

		mvc.perform(get("/screening")
				.param("startTime", searchEnd.format(DateTimeFormatter.ISO_ZONED_DATE_TIME))
				.param("endTime", searchStart.format(DateTimeFormatter.ISO_ZONED_DATE_TIME))
				.param("movieId", movieTheGodfather.getId().toString())
				.param("theaterId", theaterOne.getId().toString())
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().is(400))
		;
	}

	@Test
	void testGetAllPlayingScreenings_404_WrongMovieId() throws Exception {
		MovieDto noSuchMovie = new MovieDto(Long.MAX_VALUE, "NoSuchMovie");

		TheaterDto theaterOne = objectMapper.readValue(mvc.perform(get("/theater/name/Theater One")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andReturn()
		.getResponse()
		.getContentAsString(), TheaterDto.class);

		mvc.perform(get("/screening")
				.param("startTime", searchStart.format(DateTimeFormatter.ISO_ZONED_DATE_TIME))
				.param("endTime", searchEnd.format(DateTimeFormatter.ISO_ZONED_DATE_TIME))
				.param("movieId", noSuchMovie.getId().toString())
				.param("theaterId", theaterOne.getId().toString())
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().is(404))
		;
	}

	@Test
	void testGetAllPlayingScreenings_404_WrongTheaterId() throws Exception {
		MovieDto movieTheGodfather = objectMapper.readValue(mvc.perform(get("/movie/title/The Godfather")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andReturn()
		.getResponse()
		.getContentAsString(), MovieDto.class);

		TheaterDto noSuchTheater = new TheaterDto(Long.MAX_VALUE, "NoSuchTheater");

		mvc.perform(get("/screening")
				.param("startTime", searchEnd.format(DateTimeFormatter.ISO_ZONED_DATE_TIME))
				.param("endTime", searchStart.format(DateTimeFormatter.ISO_ZONED_DATE_TIME))
				.param("movieId", movieTheGodfather.getId().toString())
				.param("theaterId", noSuchTheater.getId().toString())
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().is(400))
		;
	}

	
}
