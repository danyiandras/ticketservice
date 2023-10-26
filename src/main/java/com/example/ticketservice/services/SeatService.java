package com.example.ticketservice.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.ticketservice.model.Screening;
import com.example.ticketservice.model.Seat;
import com.example.ticketservice.repository.SeatRepository;

@Service
public class SeatService {

	private final SeatRepository seatRepository;
	
	public SeatService(SeatRepository seatRepository) {
		this.seatRepository = seatRepository;
	}

	public List<Seat> getAllAvailableSeatsForScreening(Screening screening) {
		return seatRepository.findAvailableSeats(screening, screening.getTheater());
	}

	public List<Seat> getNAvailableSeatsForScreeningWithPessimisticLock(Screening screening, Integer numberOfSeats) {
		return seatRepository.findAvailableNSeatsWithPessimisticLock(screening.getId(), screening.getTheater().getId(), numberOfSeats);
	}

	public Optional<Seat> getById(Long id) {
		return seatRepository.findById(id);
	}

}
