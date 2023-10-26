package com.example.ticketservice.repository;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.TransactionSystemException;

import com.example.ticketservice.model.Theater;

@SpringBootTest
class TheaterRepositoryTest {

	@Autowired
	TheaterRepository theaterRepository;
	
	@Test
	void testNameIsNotEmpty() {
		Theater theater = new Theater("");
		assertThrows(TransactionSystemException.class, () -> theaterRepository.save(theater));
	}

	@Test
	void testNameIsNotNull() {
		Theater theater = new Theater(null);
		assertThrows(DataIntegrityViolationException.class, () -> theaterRepository.save(theater));
	}

	@Test
	void testNameIsUnique() {
		Theater theater1 = new Theater("Not Unique Name");
		Theater theater2 = new Theater("Not Unique Name");
		theaterRepository.save(theater1);
		assertThrows(DataIntegrityViolationException.class, () -> theaterRepository.save(theater2));
	}

}
