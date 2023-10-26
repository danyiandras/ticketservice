package com.example.ticketservice.repository;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.TransactionSystemException;

import com.example.ticketservice.model.Seat;
import com.example.ticketservice.model.Theater;

@SpringBootTest
class SeatRepositoryTest {

	@Autowired
	SeatRepository seatRepository;
	
	@Autowired
	TheaterRepository theaterRepository;
	
	@Test
	void testNameIsNotEmpty() {
		Theater theater = theaterRepository.findById(1l).orElseThrow();
		Seat seat = new Seat("", theater);
		assertThrows(TransactionSystemException.class, () -> seatRepository.save(seat));
	}

	@Test
	void testNameIsNotNull() {
		Theater theater = theaterRepository.findById(1l).orElseThrow();
		Seat seat = new Seat(null, theater);
		assertThrows(DataIntegrityViolationException.class, () -> seatRepository.save(seat));
	}

	@Test
	void testNameIsUnique() {
		Theater theater = theaterRepository.findById(1l).orElseThrow();
		Seat seat1 = new Seat("Not Unique Name", theater);
		Seat seat2 = new Seat("Not Unique Name", theater);
		seatRepository.save(seat1);
		assertThrows(DataIntegrityViolationException.class, () -> seatRepository.save(seat2));
	}

	@Test
	void testTheaterIsNotNull() {
		Seat seat = new Seat("Seat 0", null);
		assertThrows(DataIntegrityViolationException.class, () -> seatRepository.save(seat));
	}

}
