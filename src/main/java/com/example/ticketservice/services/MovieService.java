package com.example.ticketservice.services;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.ticketservice.model.Movie;
import com.example.ticketservice.repository.MovieRepository;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Service
public class MovieService {

	private final MovieRepository movieRepository;
	
	public MovieService(MovieRepository movieRepository) {
		this.movieRepository = movieRepository;
	}

	public List<Movie> getAllPlayingMovies(ZonedDateTime start, ZonedDateTime end) {
		return movieRepository.findAllPlayingMovies(start, end);
	}

	public Optional<Movie> getById(@NotNull Long id) {
		return movieRepository.findById(id);
	}

	public Optional<Movie> getByTitle(@Size(min=1) String title) {
		return movieRepository.findByTitle(title);
	}

}
