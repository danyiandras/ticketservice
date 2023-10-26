package com.example.ticketservice.services;

import java.util.List;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.example.ticketservice.TicketServiceApplicationConfigurationProperties;
import com.example.ticketservice.model.Screening;
import com.example.ticketservice.model.Seat;
import com.example.ticketservice.model.Ticket;
import com.example.ticketservice.repository.TicketRepository;

import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Min;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TicketService {

	private final TicketRepository ticketRepository;
	private final SeatService seatService;
	private final int createNTicketRetry;
	
	public TicketService(TicketRepository ticketRepository, SeatService seatService, TicketServiceApplicationConfigurationProperties config) {
		this.ticketRepository = ticketRepository;
		this.seatService = seatService;
		this.createNTicketRetry = config.getCreateNTicketRetry();
	}

	public Optional<Ticket> getById(Long id) {
		return ticketRepository.findById(id);
	}

	@Transactional
	public List<Ticket> createTickets(Screening screening, List<Seat> seats) {
		return seats.stream()
				.map(seat -> ticketRepository.save(new Ticket(screening, seat)))
				.toList();
	}

	public List<Ticket> createNTicketsWithRetry(Screening screening, @Min(1) Integer numberOfTickets) {
		for (int i = 1; i <= numberOfTickets; i++) {
			log.debug("createNTicketsWithRetry retry#: "+i);
			try {
				List<Seat> seats = seatService.getAllAvailableSeatsForScreening(screening);
				if (seats.size() < numberOfTickets) {
					throw new IllegalArgumentException("Not enough available seats: "+numberOfTickets);
				}
				return createTickets(screening, seats.subList(0, numberOfTickets));
			} catch (DataIntegrityViolationException e) {
				log.info("createNTicketsWithRetry failed for "+i);
				if (i == numberOfTickets) {
					throw e;
				}
			}
		}
		throw new DataIntegrityViolationException("Should never reach this line");
	}

	@Transactional
	public List<Ticket> createNTicketsWithPessimisticLock(Screening screening, @Min(1) Integer numberOfTickets) {
		List<Seat> seats = seatService.getNAvailableSeatsForScreeningWithPessimisticLock(screening, numberOfTickets);
		if (seats.size() != numberOfTickets) {
			throw new IllegalArgumentException("Not enough available seats: "+numberOfTickets);
		}
		return createTickets(screening, seats);
	}

}
