package com.example.ticketservice.repository;

import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.ticketservice.model.Movie;
import com.example.ticketservice.model.Screening;
import com.example.ticketservice.model.Theater;

public interface ScreeningRepository extends JpaRepository<Screening, Long> {
	
	@Query("SELECT s FROM Screening s WHERE :movie = s.movie AND :theater = s.theater "
			+ "AND :startTime <= s.startTime AND s.startTime <= :endTime "
			+ "AND :startTime <= s.endTime AND s.endTime <= :endTime")
	List<Screening> getAllScreeningsOfMovieInTheaterPlaying(
			@Param("movie") Movie movie, 
			@Param("theater") Theater theater, 
			@Param("startTime") ZonedDateTime startTime, 
			@Param("endTime") ZonedDateTime endTime);

}
