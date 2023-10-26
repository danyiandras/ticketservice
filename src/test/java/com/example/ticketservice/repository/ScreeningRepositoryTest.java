package com.example.ticketservice.repository;

import static com.example.ticketservice.TestConstants.searchEnd;
import static com.example.ticketservice.TestConstants.searchStart;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

import com.example.ticketservice.model.Movie;
import com.example.ticketservice.model.Screening;
import com.example.ticketservice.model.Theater;

@SpringBootTest
class ScreeningRepositoryTest {

	@Autowired
	MovieRepository movieRepository;
	
	@Autowired
	TheaterRepository theaterRepository;
	
	@Autowired
	ScreeningRepository screeningRepository;
	
//	@Test
//	void testTimeIntervallIsNotValid() {
//		Movie movie = movieRepository.findById(1l).orElseThrow();
//		Theater theater = theaterRepository.findById(1l).orElseThrow();
//		Screening screening = new Screening(movie, theater, searchEnd, searchStart);
//		assertThrows(DataIntegrityViolationException.class, () -> screeningRepository.save(screening));
//	}
//
//	@Test
//	void testTimeIntervallsOverlap() {
//		Movie movie = movieRepository.findById(1l).orElseThrow();
//		Theater theater = theaterRepository.findById(1l).orElseThrow();
//		Screening screening1 = new Screening(movie, theater, searchEnd, searchStart);
//		Screening screening2 = new Screening(movie, theater, searchEnd, searchStart.plusHours(1));
//		screeningRepository.save(screening1);
//		assertThrows(DataIntegrityViolationException.class, () -> screeningRepository.save(screening2));
//	}

	@Test
	void testMovieIsNull() {
		Theater theater = theaterRepository.findById(1l).orElseThrow();
		Screening screening = new Screening(null, theater, searchEnd, searchStart);
		assertThrows(DataIntegrityViolationException.class, () -> screeningRepository.save(screening));
	}

	@Test
	void testTheaterIsNull() {
		Movie movie = movieRepository.findById(1l).orElseThrow();
		Screening screening = new Screening(movie, null, searchEnd, searchStart);
		assertThrows(DataIntegrityViolationException.class, () -> screeningRepository.save(screening));
	}

}
