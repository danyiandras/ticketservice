package com.example.ticketservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.ticketservice.model.Screening;
import com.example.ticketservice.model.Seat;
import com.example.ticketservice.model.Theater;

import jakarta.validation.constraints.Min;

public interface SeatRepository extends JpaRepository<Seat, Long> {

	@Query("SELECT seat FROM Seat seat WHERE seat.theater = :theater AND "
			+ "seat NOT IN (SELECT soldSeat FROM Ticket t INNER JOIN t.seat soldSeat WHERE t.screening = :screening)")
	List<Seat> findAvailableSeats(Screening screening, Theater theater);

	@Query(value = "SELECT s.* "
			+ "FROM SEAT s "
			+ "WHERE s.THEATER_ID = :theaterId AND "
			+ "	s.SEAT_ID NOT IN "
			+ "  (SELECT t.SEAT_ID "
			+ "     FROM TICKET t "
			+ "     WHERE t.SCREENING_ID = :screeningId"
			+ "  ) LIMIT :numberOfSeats FOR UPDATE",
			nativeQuery = true)
	List<Seat> findAvailableNSeatsWithPessimisticLock(
			@Param("screeningId") Long screeningId, 
			@Param("theaterId") Long theaterId, 
			@Param("numberOfSeats") @Min(1) Integer numberOfSeats);
}
