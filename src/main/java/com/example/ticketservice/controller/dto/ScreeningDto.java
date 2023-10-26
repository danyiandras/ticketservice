package com.example.ticketservice.controller.dto;

import java.time.ZonedDateTime;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.example.ticketservice.model.Screening;
import com.example.ticketservice.services.ScreeningService;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ScreeningDto implements MappedDto<Screening, ScreeningDto> {
	@NotNull
	Long id;

	@NotNull
	ZonedDateTime startTime;
	@NotNull
	ZonedDateTime endTime;
	@NotNull
	Long movieId;
	@Size(min=1, max=256)
	@NotNull
	String movieTitle;
	@NotNull
	Long theaterId;
	@Size(min=1, max=256)
	@NotNull
	String theaterName;
	
	public Screening convertToEntity(ScreeningService screeningService, ModelMapper mapper) {
		return MappedDto.super.convertToEntity(id -> 
			screeningService.getById(this.id).orElseThrow(() -> 
				new ResponseStatusException(HttpStatus.NOT_FOUND, "Screening id not found: "+this.getId())),
			mapper);
	}

}
