package com.example.ticketservice.controller.dto;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.example.ticketservice.model.Movie;
import com.example.ticketservice.services.MovieService;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class MovieDto implements MappedDto<Movie, MovieDto> {

	@NotNull
	Long id;

	@NotNull
	@Size(min=1, max=256)
	String title;

	public Movie convertToEntity(MovieService movieService, ModelMapper mapper) {
		return MappedDto.super.convertToEntity(id -> 
			movieService.getById(this.id).orElseThrow(() -> 
				new ResponseStatusException(HttpStatus.NOT_FOUND, "Movie id not found: "+this.getId())), 
			mapper);
	}

}
