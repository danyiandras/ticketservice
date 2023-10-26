package com.example.ticketservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.ticketservice.model.Ticket;

import jakarta.validation.constraints.Min;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

	@Query(value = 
		"INSERT INTO TICKETS "
		+ "SELECT :screeningId, seat.SEAT_ID "
		+ "FROM SEAT seat WHERE seat.THEATER_ID = :theaterId AND "
		+ "	seat.SEAT_ID NOT IN "
		+ "  (SELECT t.SEAT_ID "
		+ "     FROM TICKET t "
		+ "     WHERE t.SCREENING_ID = :screeningId"
		+ "  ) LIMIT :numberOfTickets",
		nativeQuery = true)
	List<Ticket> createTickets(
			@Param("screeningId") Long screeningId, 
			@Param("theaterId") Long theaterId, 
			@Param("numberOfTickets") @Min(1) Integer numberOfTickets);
}
