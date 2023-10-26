package com.example.ticketservice.controller;

import static com.example.ticketservice.controller.RestControllerUtil.validateTimeIntervall;

import java.time.ZonedDateTime;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.ticketservice.controller.dto.MappedDto;
import com.example.ticketservice.controller.dto.MovieDto;
import com.example.ticketservice.services.MovieService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

@RestController
@RequestMapping("/movie")
@Validated
public class MovieController {

	private final MovieService movieService;
	private final ModelMapper modelMapper;
	
	public MovieController(MovieService movieService, ModelMapper modelMapper) {
		this.movieService = movieService;
		this.modelMapper = modelMapper;
	}

	@Operation(summary = "Get a Movie by its id.")
	@ApiResponses(value = { 
	  @ApiResponse(responseCode = "200", description = "Found the Movie.", 
	    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = MovieDto.class)) }),
	  @ApiResponse(responseCode = "400", description = "Invalid id supplied.", content = @Content), 
	  @ApiResponse(responseCode = "404", description = "Movie not found.", content = @Content) 
	  })
	@GetMapping("/{id}")
	MovieDto getById(
			@Parameter(description = "ID of Movie to be searched.") 
			@PathVariable("id") @Min(1) Long id
	) {
		return MappedDto.convertToDto(movieService.getById(id).orElseThrow(), MovieDto.class, modelMapper);
	}

	@Operation(summary = "Get a Movie by its title.")
	@ApiResponses(value = { 
	  @ApiResponse(responseCode = "200", description = "Found the Movie.", 
	    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = MovieDto.class)) }),
	  @ApiResponse(responseCode = "404", description = "Movie not found.", content = @Content) 
	  })
	@GetMapping("/title/{title}")
	MovieDto getByTitle(
			@Parameter(description = "Title of Movie to be searched.") 
			@PathVariable("title") @Size(min=1, max=256) String title
	) {
		return MappedDto.convertToDto(movieService.getByTitle(title).orElseThrow(), MovieDto.class, modelMapper);
	}

	@Operation(summary = "Get Movies playing in the given time intervall.")
	@ApiResponses(value = { 
	  @ApiResponse(responseCode = "200", description = "Found some (maybe zero) Movies.", 
	    content = { @Content(mediaType = "application/json", 
	    array = @ArraySchema(schema = @Schema(implementation = MovieDto.class))
	    ) }),
	  @ApiResponse(responseCode = "400", description = "Invalid time intervall.", content = @Content) 
	  })
	@GetMapping
	List<MovieDto> getAllPlayingMovies(
			@Parameter(description = "start of the time interval in which the Movie is played.") 
			@RequestParam("startTime") ZonedDateTime startTime, 
			@Parameter(description = "end of the time interval in which the Movie is played.") 
			@RequestParam("endTime") ZonedDateTime endTime
	) {
		validateTimeIntervall(startTime, endTime);
		return movieService.getAllPlayingMovies(startTime, endTime).stream()
				.<MovieDto>map(movie -> MappedDto.convertToDto(movie, MovieDto.class, modelMapper))
				.toList()
				;
	}

}
