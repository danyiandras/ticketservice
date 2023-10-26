package com.example.ticketservice.repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.ticketservice.model.Movie;
import com.example.ticketservice.model.Theater;

import jakarta.validation.constraints.Size;

public interface TheaterRepository extends JpaRepository<Theater, Long> {

	@Query("SELECT t FROM Screening s INNER JOIN s.theater t WHERE :movie = s.movie "
			+ "AND :startTime <= s.startTime AND s.startTime <= :endTime "
			+ "AND :startTime <= s.endTime AND s.endTime <= :endTime")
	List<Theater> findByMovieAndByStartTimeBetweenAndEndTimeBetween(
			@Param("movie") Movie movie, 
			@Param("startTime") ZonedDateTime startTime, 
			@Param("endTime") ZonedDateTime endTime);

	Optional<Theater> findByName(@Size(min = 1, max = 256) String name);

}
