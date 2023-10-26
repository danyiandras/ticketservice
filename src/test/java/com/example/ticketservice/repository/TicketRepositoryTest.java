package com.example.ticketservice.repository;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

import com.example.ticketservice.model.Screening;
import com.example.ticketservice.model.Seat;
import com.example.ticketservice.model.Ticket;

@SpringBootTest
class TicketRepositoryTest {

	@Autowired
	TicketRepository ticketRepository;	

	@Autowired
	ScreeningRepository screeningRepository;

	@Autowired
	SeatRepository seatRepository;
	
	@Test
	void testScreeningIsNull() {
		Seat seat = seatRepository.findById(1l).orElseThrow();
		Ticket ticket = new Ticket(null, seat);
		assertThrows(DataIntegrityViolationException.class, () -> ticketRepository.save(ticket));
	}

	@Test
	void testSeatIsNull() {
		Screening screening = screeningRepository.findById(1l).orElseThrow();
		Ticket ticket = new Ticket(screening, null);
		assertThrows(DataIntegrityViolationException.class, () -> ticketRepository.save(ticket));
	}

	@Test
	void testSeatCanNotBookedTwice() {
		Screening screening = screeningRepository.findById(1l).orElseThrow();
		Seat seat = seatRepository.findById(1l).orElseThrow();
		Ticket ticket1 = new Ticket(screening, seat);
		Ticket ticket2 = new Ticket(screening, seat);
		ticketRepository.save(ticket1);
		assertThrows(DataIntegrityViolationException.class, () -> ticketRepository.save(ticket2));
	}


}
