package com.example.ticketservice.controller;

import static com.example.ticketservice.controller.RestControllerUtil.validateTimeIntervall;

import java.time.ZonedDateTime;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.ticketservice.controller.dto.MappedDto;
import com.example.ticketservice.controller.dto.ScreeningDto;
import com.example.ticketservice.model.Movie;
import com.example.ticketservice.model.Theater;
import com.example.ticketservice.services.MovieService;
import com.example.ticketservice.services.ScreeningService;
import com.example.ticketservice.services.TheaterService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.Min;

@RestController
@RequestMapping("/screening")
@Validated
public class ScreeningController {

	private final ScreeningService screeningService;
	private final MovieService movieService;
	private final TheaterService theaterService;
	private final ModelMapper modelMapper;
	
	public ScreeningController(ScreeningService screeningService, MovieService movieService, TheaterService theaterService, ModelMapper modelMapper) {
		this.screeningService = screeningService;
		this.movieService = movieService;
		this.theaterService = theaterService;
		this.modelMapper = modelMapper;
	}

	@Operation(summary = "Get a Screening by its id.")
	@ApiResponses(value = { 
	  @ApiResponse(responseCode = "200", description = "Found the Screening.", 
	    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ScreeningDto.class)) }),
	  @ApiResponse(responseCode = "400", description = "Invalid id supplied.", content = @Content), 
	  @ApiResponse(responseCode = "404", description = "Screening not found.", content = @Content) 
	  })
	@GetMapping("/{id}")
	ScreeningDto getById(
			@Parameter(description = "ID of Screening to be searched.") 
			@PathVariable("id") @Min(1) Long id
	) {
		return MappedDto.convertToDto(screeningService.getById(id).orElseThrow(), ScreeningDto.class, modelMapper);
	}

	@Operation(summary = "Get Screenings of the given Movie in the given Theater in the given time intervall.")
	@ApiResponses(value = { 
	  @ApiResponse(responseCode = "200", description = "Found some (maybe zero) Screenings.", 
	    content = { @Content(mediaType = "application/json", 
	    array = @ArraySchema(schema = @Schema(implementation = ScreeningDto.class))
	    ) }),
	  @ApiResponse(responseCode = "400", description = "Invalid time intervall.", content = @Content),
	  @ApiResponse(responseCode = "404", description = "The given Movie or Theater not found.", content = @Content) 
	  })
	@GetMapping
	List<ScreeningDto> getAllScreeningsOfMovieInTheaterPlaying(
			@Parameter(description = "start of the time interval in which the Screening is played.") 
			@RequestParam("startTime") ZonedDateTime startTime, 
			@Parameter(description = "end of the time interval in which the Screening is played.") 
			@RequestParam("endTime") ZonedDateTime endTime,
			@Parameter(description = "The id of the Movie of the Screening.") 
			@RequestParam("movieId") Long movieId,
			@Parameter(description = "The id of the Theatre of the Screening.") 
			@RequestParam("theaterId") Long theaterId
	) {
		validateTimeIntervall(startTime, endTime);
		Movie movie = movieService.getById(movieId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Movie id not found: "+movieId));
		Theater theater = theaterService.getById(theaterId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Theater id not found: "+theaterId));
		return screeningService.getAllScreeningsOfMovieInTheaterPlaying(movie, theater, startTime, endTime).stream()
				.<ScreeningDto>map(screening -> MappedDto.convertToDto(screening, ScreeningDto.class, modelMapper))
				.toList()
				;
	}

}
