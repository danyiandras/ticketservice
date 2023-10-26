package com.example.ticketservice.services;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.ticketservice.model.Movie;
import com.example.ticketservice.model.Screening;
import com.example.ticketservice.model.Theater;
import com.example.ticketservice.repository.ScreeningRepository;

@Service
public class ScreeningService {

	private final ScreeningRepository screeningRepository;
	
	public ScreeningService(ScreeningRepository screeningRepository) {
		this.screeningRepository = screeningRepository;
	}

	public List<Screening> getAllScreeningsOfMovieInTheaterPlaying(
			Movie movie, Theater theater,
			ZonedDateTime start, ZonedDateTime end
	) {
		return screeningRepository.getAllScreeningsOfMovieInTheaterPlaying(movie, theater, start, end);
	}

	public Optional<Screening> getById(Long id) {
		return screeningRepository.findById(id);
	}

}
