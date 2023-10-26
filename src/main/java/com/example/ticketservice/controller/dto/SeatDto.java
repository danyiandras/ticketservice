package com.example.ticketservice.controller.dto;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.example.ticketservice.model.Seat;
import com.example.ticketservice.services.SeatService;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SeatDto implements MappedDto<Seat, SeatDto> {
	@NotNull
	Long id;
	@Size(min=1, max=256)
	@NotNull
	String name;
	@NotNull
	Long theaterId;	
	@Size(min=1, max=256)
	@NotNull
	String theaterName;	

	public Seat convertToEntity(SeatService seatService, ModelMapper mapper) {
		return MappedDto.super.convertToEntity(id -> 
			seatService.getById(this.id).orElseThrow(() -> 
				new ResponseStatusException(HttpStatus.NOT_FOUND, "Seat id not found: "+this.getId())),
			mapper);
	}

}
