package com.example.ticketservice.controller.dto;

import java.time.ZonedDateTime;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.example.ticketservice.model.Ticket;
import com.example.ticketservice.services.TicketService;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TicketDto implements MappedDto<Ticket, TicketDto> {
	@NotNull
	Long id;

	@NotNull
	Long seatId;
	@Size(min=1, max=256)
	@NotNull
	String seatName;

	@NotNull
	Long screeningId;
	@Size(min=1, max=256)
	@NotNull
	String screeningMovieTitle;
	@Size(min=1, max=256)
	@NotNull
	String screeningTheaterName;
	@NotNull
	ZonedDateTime screeningStartTime;
	@NotNull
	ZonedDateTime screeningEndTime;

	public Ticket convertToEntity(TicketService ticketService, ModelMapper mapper) {
		return MappedDto.super.convertToEntity(id -> 
			ticketService.getById(this.id).orElseThrow(() -> 
				new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket id not found: "+this.getId())), 
			mapper);
	}

}
