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
import com.example.ticketservice.controller.dto.TheaterDto;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
class TheaterControllerTest {

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private MockMvc mvc;

	@Test
	void testGetById() throws Exception {
		TheaterDto theaterOne = objectMapper.readValue(mvc.perform(get("/theater/1")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andReturn()
		.getResponse()
		.getContentAsString(), TheaterDto.class);
		
		assertEquals(1, theaterOne.getId());
	}

	@Test
	void testGetById_400() throws Exception {
		mvc.perform(get("/theater/0")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().is(400));
	}

	@Test
	void testGetById_404() throws Exception {
		mvc.perform(get("/theater/"+Integer.MAX_VALUE)
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().is(404));		
	}

	@Test
	void testGetByName() throws Exception {
		TheaterDto theaterOne = objectMapper.readValue(mvc.perform(get("/theater/name/Theater One")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andReturn()
		.getResponse()
		.getContentAsString(), TheaterDto.class);
		
		assertEquals("Theater One", theaterOne.getName());
	}

	@Test
	void testGetByName_404() throws Exception {
		mvc.perform(get("/theater/name/NoSuchTheater")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().is(404));		
	}

	@Test
	void testGetAllPlayingTheaters() throws Exception {
		MovieDto movieOne = objectMapper.readValue(mvc.perform(get("/movie/1")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andReturn()
		.getResponse()
		.getContentAsString(), MovieDto.class);

		TheaterDto[] theatersPlaying = objectMapper.readValue(mvc.perform(get("/theater")
				.param("startTime", searchStart.format(DateTimeFormatter.ISO_ZONED_DATE_TIME))
				.param("endTime", searchEnd.format(DateTimeFormatter.ISO_ZONED_DATE_TIME))
				.param("movieId", movieOne.getId().toString())
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andReturn()
		.getResponse()
		.getContentAsString(), TheaterDto[].class);
		
		TheaterDto theaterOne = objectMapper.readValue(mvc.perform(get("/theater/name/Theater One")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andReturn()
		.getResponse()
		.getContentAsString(), TheaterDto.class);

		TheaterDto theaterTwo = objectMapper.readValue(mvc.perform(get("/theater/name/Theater Two")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andReturn()
		.getResponse()
		.getContentAsString(), TheaterDto.class);

		//[TheaterDto(id=1, name=Theater One), TheaterDto(id=2, name=Theater Two)]
		assertEquals(List.of(theaterOne, theaterTwo), List.of(theatersPlaying));
	}

	@Test
	void testGetAllPlayingTheaters_400() throws Exception {
		MovieDto movieOne = objectMapper.readValue(mvc.perform(get("/movie/1")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andReturn()
		.getResponse()
		.getContentAsString(), MovieDto.class);

		mvc.perform(get("/theater")
				.param("startTime", searchEnd.format(DateTimeFormatter.ISO_ZONED_DATE_TIME))
				.param("endTime", searchStart.format(DateTimeFormatter.ISO_ZONED_DATE_TIME))
				.param("movieId", movieOne.getId().toString())
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().is(400))
		;
	}

	@Test
	void testGetAllPlayingTheaters_404() throws Exception {
		MovieDto nonExistentMovie = new MovieDto(Long.MAX_VALUE, "Non Existent Movie");

		mvc.perform(get("/theater")
				.param("startTime", searchStart.format(DateTimeFormatter.ISO_ZONED_DATE_TIME))
				.param("endTime", searchEnd.format(DateTimeFormatter.ISO_ZONED_DATE_TIME))
				.param("movieId", nonExistentMovie.getId().toString())
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().is(404))
		;
	}

}
