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
import com.example.ticketservice.controller.dto.TheaterDto;
import com.example.ticketservice.model.Movie;
import com.example.ticketservice.services.MovieService;
import com.example.ticketservice.services.TheaterService;

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
@RequestMapping("/theater")
@Validated
public class TheaterController {

	private final TheaterService theaterService;
	private final MovieService movieService;
	private final ModelMapper modelMapper;
	
	public TheaterController(TheaterService theaterService, MovieService movieService, ModelMapper modelMapper) {
		this.theaterService = theaterService;
		this.movieService = movieService;
		this.modelMapper = modelMapper;
	}

	@Operation(summary = "Get a Theater by its id.")
	@ApiResponses(value = { 
	  @ApiResponse(responseCode = "200", description = "Found the Theater.", 
	    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = TheaterDto.class)) }),
	  @ApiResponse(responseCode = "400", description = "Invalid id supplied.", content = @Content), 
	  @ApiResponse(responseCode = "404", description = "Theater not found.", content = @Content) 
	  })
	@GetMapping("/{id}")
	TheaterDto getById(
			@Parameter(description = "ID of Theater to be searched.") 
			@PathVariable("id") @Min(1) Long id
	) {
		return MappedDto.convertToDto(theaterService.getById(id).orElseThrow(), TheaterDto.class, modelMapper);
	}

	@Operation(summary = "Get a Theater by its name.")
	@ApiResponses(value = { 
	  @ApiResponse(responseCode = "200", description = "Found the Theater.", 
	    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = TheaterDto.class)) }),
	  @ApiResponse(responseCode = "404", description = "Theater not found.", content = @Content) 
	  })
	@GetMapping("/name/{name}")
	TheaterDto getByName(
			@Parameter(description = "Name of Theater to be searched.") 
			@PathVariable("name") @Size(min=1, max=256) String name
	) {
		return MappedDto.convertToDto(theaterService.getByName(name).orElseThrow(), TheaterDto.class, modelMapper);
	}

	@Operation(summary = "Get Theaters playing the given Movie in the given time intervall.")
	@ApiResponses(value = { 
	  @ApiResponse(responseCode = "200", description = "Found some (maybe zero) Theaters.", 
	    content = { @Content(mediaType = "application/json", 
	    array = @ArraySchema(schema = @Schema(implementation = TheaterDto.class))
	    )}),
	  @ApiResponse(responseCode = "400", description = "Invalid time intervall.", content = @Content),
	  @ApiResponse(responseCode = "404", description = "The given Movie not found.", content = @Content) 
	  })
	@GetMapping
	List<TheaterDto> getAllPlayingTheaters(
			@Parameter(description = "start of the time interval in which the Movie is played.") 
			@RequestParam("startTime") ZonedDateTime startTime, 
			@Parameter(description = "end of the time interval in which the Movie is played.") 
			@RequestParam("endTime") ZonedDateTime endTime,
			@Parameter(description = "ID of Movie we are looking for.") 
			@RequestParam("movieId") Long movieId
	) {
		validateTimeIntervall(startTime, endTime);
		Movie movie = movieService.getById(movieId)
				.orElseThrow();
		return theaterService.getAllTheatersPlayingTheMovie(movie, startTime, endTime).stream()
				.<TheaterDto>map(theater -> MappedDto.convertToDto(theater, TheaterDto.class, modelMapper))
				.toList()
				;
	}

}
