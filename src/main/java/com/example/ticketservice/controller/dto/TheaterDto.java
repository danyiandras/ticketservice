package com.example.ticketservice.controller.dto;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.example.ticketservice.model.Theater;
import com.example.ticketservice.services.TheaterService;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class TheaterDto implements MappedDto<Theater, TheaterDto> {
	@NotNull
	Long id;
	@Size(min=1, max=256)
	@NotNull
	String name;

	public Theater convertToEntity(TheaterService theaterService, ModelMapper mapper) {
		return MappedDto.super.convertToEntity(id -> 
			theaterService.getById(this.id).orElseThrow(() -> 
				new ResponseStatusException(HttpStatus.NOT_FOUND, "Theater id not found: "+this.getId())), 
			mapper);
	}

}
