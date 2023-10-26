package com.example.ticketservice.repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.ticketservice.model.Movie;

import jakarta.validation.constraints.Size;

public interface MovieRepository extends JpaRepository<Movie, Long> {
	
	@Query("SELECT m FROM Screening s INNER JOIN s.movie m WHERE :startTime <= s.startTime AND s.startTime <= :endTime "
			+ "AND :startTime <= s.endTime AND s.endTime <= :endTime")
	List<Movie> findAllPlayingMovies(
			@Param("startTime") ZonedDateTime startTime, 
			@Param("endTime") ZonedDateTime endTime);

	Optional<Movie> findByTitle(@Size(min=1) String title);

}
