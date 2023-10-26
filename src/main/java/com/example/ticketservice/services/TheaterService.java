package com.example.ticketservice.services;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.ticketservice.model.Movie;
import com.example.ticketservice.model.Theater;
import com.example.ticketservice.repository.TheaterRepository;

import jakarta.validation.constraints.Size;

@Service
public class TheaterService {

	private final TheaterRepository theaterRepository;
	
	public TheaterService(TheaterRepository theaterRepository) {
		this.theaterRepository = theaterRepository;
	}

	public List<Theater> getAllTheatersPlayingTheMovie(Movie movie, ZonedDateTime start, ZonedDateTime end) {
		return theaterRepository.findByMovieAndByStartTimeBetweenAndEndTimeBetween(movie, start, end);
	}

	public Optional<Theater> getById(Long id) {
		return theaterRepository.findById(id);
	}

	public Optional<Theater> getByName(@Size(min = 1, max = 256) String name) {
		return theaterRepository.findByName(name);
	}

}
