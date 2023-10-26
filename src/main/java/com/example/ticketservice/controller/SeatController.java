package com.example.ticketservice.controller;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.ticketservice.controller.dto.MappedDto;
import com.example.ticketservice.controller.dto.SeatDto;
import com.example.ticketservice.model.Screening;
import com.example.ticketservice.services.ScreeningService;
import com.example.ticketservice.services.SeatService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.Min;

@RestController
@RequestMapping("/seat")
@Validated
public class SeatController {

	private final SeatService seatService;
	private final ScreeningService screeningService;
	private final ModelMapper modelMapper;
	
	public SeatController(SeatService seatService, ScreeningService screeningService, ModelMapper modelMapper) {
		this.seatService = seatService;
		this.screeningService = screeningService;
		this.modelMapper = modelMapper;
	}

	@Operation(summary = "Get a Seat by its id.")
	@ApiResponses(value = { 
	  @ApiResponse(responseCode = "200", description = "Found the Seat.", 
	    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = SeatDto.class)) }),
	  @ApiResponse(responseCode = "400", description = "Invalid id supplied.", content = @Content), 
	  @ApiResponse(responseCode = "404", description = "Seat not found.", content = @Content) 
	  })
	@GetMapping("/{id}")
	SeatDto getById(
			@Parameter(description = "ID of Seat to be searched.") 
			@PathVariable("id") @Min(1) Long id
	) {
		return MappedDto.convertToDto(seatService.getById(id).orElseThrow(), SeatDto.class, modelMapper);
	}

	@Operation(summary = "Get all available Seats for the given Screening.")
	@ApiResponses(value = { 
	  @ApiResponse(responseCode = "200", description = "Found some available (maybe zero) Seats.", 
	    content = { @Content(mediaType = "application/json", 
	    array = @ArraySchema(schema = @Schema(implementation = SeatDto.class))
	    ) }),
	  @ApiResponse(responseCode = "404", description = "The given Screening not found.", content = @Content) 
	  })
	@GetMapping
	List<SeatDto> getAllAvailableSeatsForScreening(
			@Parameter(description = "ID of Screening we search available Seats for.") 
			@RequestParam("screeningId") Long screeningId
	) {
		Screening screening = screeningService.getById(screeningId)
				.orElseThrow();
		return seatService.getAllAvailableSeatsForScreening(screening).stream()
				.<SeatDto>map(seat -> MappedDto.convertToDto(seat, SeatDto.class, modelMapper))
				.toList()
				;
	}
}
