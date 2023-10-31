package com.example.ticketservice.services;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.retry.RetryContext;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.retry.support.RetryTemplateBuilder;
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
	private final RetryTemplate retryTemplate;
	
	public TicketService(TicketRepository ticketRepository, SeatService seatService, TicketServiceApplicationConfigurationProperties config) {
		this.ticketRepository = ticketRepository;
		this.seatService = seatService;
		this.retryTemplate = new RetryTemplateBuilder()
				.fixedBackoff(Duration.ofMillis(1))
				.maxAttempts(config.getCreateNTicketRetry())
				.retryOn(List.of(DataIntegrityViolationException.class))
				.build();
	}

	public Optional<Ticket> getById(Long id) {
		return ticketRepository.findById(id);
	}

	public List<Ticket> createTickets(Screening screening, List<Seat> seats) {
		List<Ticket> tickets = seats.stream()
		.map(seat -> new Ticket(screening, seat))
		.toList();
		return ticketRepository.saveAll(tickets);
	}

    @Retryable(retryFor = DataIntegrityViolationException.class, 
    		maxAttemptsExpression = "#{${ticketservice.create-n-ticket-retry}}", 
    		backoff = @Backoff(delay = 1, maxDelay = 1))
	public List<Ticket> createNTicketsWithRetry(Screening screening, @Min(1) Integer numberOfTickets) {
		log.debug("createNTicketsWithRetry");
		List<Seat> seats = seatService.getAllAvailableSeatsForScreening(screening);
		if (seats.size() < numberOfTickets) {
			throw new IllegalArgumentException("Not enough available seats: " + numberOfTickets);
		}
//		createTickets(screening, seats.subList(0, 1));
		return createTickets(screening, seats.subList(0, numberOfTickets));
	}

	public List<Ticket> createNTicketsWithRetryTemplate(Screening screening, @Min(1) Integer numberOfTickets) {
		return retryTemplate.execute(c -> _createNTicketsWithRetryTemplate(c, screening, numberOfTickets));
	}

	private List<Ticket> _createNTicketsWithRetryTemplate(RetryContext retryContext,  Screening screening, Integer numberOfTickets) {
		log.info("createNTicketsWithRetry"+retryContext.getRetryCount());
		List<Seat> seats = seatService.getAllAvailableSeatsForScreening(screening);
		if (seats.size() < numberOfTickets) {
			throw new IllegalArgumentException("Not enough available seats: " + numberOfTickets);
		}
		return createTickets(screening, seats.subList(0, numberOfTickets));
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
