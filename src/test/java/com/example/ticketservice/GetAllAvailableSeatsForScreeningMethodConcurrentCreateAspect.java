package com.example.ticketservice;

import java.util.List;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import com.example.ticketservice.model.Screening;
import com.example.ticketservice.model.Seat;
import com.example.ticketservice.services.TicketService;

import lombok.extern.slf4j.Slf4j;

@Component
@Aspect
@Slf4j
public class GetAllAvailableSeatsForScreeningMethodConcurrentCreateAspect {
	
	private final TicketService ticketService;
	
	public GetAllAvailableSeatsForScreeningMethodConcurrentCreateAspect(TicketService ticketService) {
    	this.ticketService = ticketService;
	}
	
	public boolean on = false;

	@Pointcut("execution(* com.example.ticketservice.services.SeatService.getAllAvailableSeatsForScreening(..))")
    public void getAllAvailableSeatsForScreeningMethod() {};
	
    @AfterReturning(value = "getAllAvailableSeatsForScreeningMethod()", returning = "seatList")
    public void getAllAvailableSeatsForScreeningMethodConcurrentCreate(JoinPoint jp, Object seatList) throws Throwable {
    	if (!this.on) {
    		return;
    	}

    	Screening screening = (Screening) jp.getArgs()[0];
		@SuppressWarnings("unchecked")
		List<Seat> seats = (List<Seat>) seatList;
    	log.info("GetAllAvailableSeatsMethodAspect: {}, {}", seats.size(), this.on);
		if (seats.size() > 0) {
			ticketService.createTickets(screening, seats.subList(0, 1));
		}
    }
}