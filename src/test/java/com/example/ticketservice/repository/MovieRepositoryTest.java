package com.example.ticketservice.repository;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.TransactionSystemException;

import com.example.ticketservice.model.Movie;

@SpringBootTest
class MovieRepositoryTest {

	@Autowired
	MovieRepository movieRepository;
	
	@Test
	void testNameIsNotEmpty() {
		Movie movie = new Movie("");
		assertThrows(TransactionSystemException.class, () -> movieRepository.save(movie));
	}

	@Test
	void testNameIsNotNull() {
		Movie movie = new Movie(null);
		assertThrows(DataIntegrityViolationException.class, () -> movieRepository.save(movie));
	}

	@Test
	void testNameIsUnique() {
		Movie movie1 = new Movie("Not Unique Title");
		Movie movie2 = new Movie("Not Unique Title");
		movieRepository.save(movie1);
		assertThrows(DataIntegrityViolationException.class, () -> movieRepository.save(movie2));
	}

}
