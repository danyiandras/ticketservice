package com.example.ticketservice.controller;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.ticketservice.controller.dto.MappedDto;
import com.example.ticketservice.controller.dto.TicketDto;
import com.example.ticketservice.controller.dto.TicketOrderDto;
import com.example.ticketservice.model.Screening;
import com.example.ticketservice.model.Seat;
import com.example.ticketservice.services.ScreeningService;
import com.example.ticketservice.services.SeatService;
import com.example.ticketservice.services.TicketService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.Min;

@RestController
@RequestMapping("/ticket")
@Validated
public class TicketController {

	private final TicketService ticketService;
	private final ScreeningService screeningService;
	private final SeatService seatService;
	private final ModelMapper modelMapper;
	
	public TicketController(TicketService ticketService, ScreeningService screeningService, SeatService seatService, ModelMapper modelMapper) {
		this.ticketService = ticketService;
		this.screeningService = screeningService;
		this.seatService = seatService;
		this.modelMapper = modelMapper;
	}

	@Operation(summary = "Get a Ticket by its id.")
	@ApiResponses(value = { 
	  @ApiResponse(responseCode = "200", description = "Found the Ticket.", 
	    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = TicketDto.class)) }),
	  @ApiResponse(responseCode = "400", description = "Invalid id supplied.", content = @Content), 
	  @ApiResponse(responseCode = "404", description = "Ticket not found.", content = @Content) 
	  })
	@GetMapping("/{id}")
	TicketDto getById(
			@Parameter(description = "ID of Ticket to be searched.") 
			@PathVariable("id") @Min(1) Long id
	) {
		return MappedDto.convertToDto(ticketService.getById(id).orElseThrow(), TicketDto.class, modelMapper);
	}

	@Operation(summary = "Creates Tickets for the Screening and Seats.")
	@ApiResponses(value = { 
	  @ApiResponse(responseCode = "201", description = "Successfully created all Tickets.", 
	    content = { @Content(mediaType = "application/json", 
	    array = @ArraySchema(schema = @Schema(implementation = TicketDto.class))
	    )}),
	  @ApiResponse(responseCode = "404", description = "The given Screening or one of the Seats not found.", content = @Content) 
	  })
	@PostMapping("/book")
	@ResponseStatus(HttpStatus.CREATED)
	List<TicketDto> createTickets(
			@Parameter(description = "The TicketOrder containing the ID of the "
					+ "Screening and the Seats we want to create tickets for.") 
			@RequestBody TicketOrderDto ticketOrderDto
	) {
		Screening screening = screeningService.getById(ticketOrderDto.getScreeningId())
				.orElseThrow(() -> new ResponseStatusException(
						HttpStatus.NOT_FOUND, "Screening id not found: "+ticketOrderDto.getScreeningId()));
		List<Seat> seats = ticketOrderDto.getSeatIds().stream()
				.map(seatId -> seatService.getById(seatId).orElseThrow(() -> new ResponseStatusException(
						HttpStatus.NOT_FOUND, "Seat id not found: "+seatId)))
				.toList();
		return ticketService.createTickets(screening, seats).stream()
				.<TicketDto>map(ticket -> MappedDto.convertToDto(ticket, TicketDto.class,  modelMapper))
				.toList()
				;
	}

	@Operation(summary = "Creates the given number of Tickets for the Screening with some available Seats.")
	@ApiResponses(value = { 
	  @ApiResponse(responseCode = "201", description = "Successfully created all Tickets.", 
	    content = { @Content(mediaType = "application/json", 
	    array = @ArraySchema(schema = @Schema(implementation = TicketDto.class))
	    )}),
	  @ApiResponse(responseCode = "400", description = "Invalid number of Tickets supplied or there are not enough available Seats.", content = @Content), 
	  @ApiResponse(responseCode = "404", description = "The given Screening not found.", content = @Content) 
	  })
	@PostMapping("/bookn")
	@ResponseStatus(HttpStatus.CREATED)
	List<TicketDto> createNTickets(
			@Parameter(description = "ID of Screening we are to create Tickets for.") 
			@RequestParam("screeningId") Long screeningId,
			@Parameter(description = "Number of Ticket to be created.") 
			@RequestParam("numberOfTickets") @Min(1) Integer numberOfTickets
	) {
		Screening screening = screeningService.getById(screeningId)
				.orElseThrow(() -> new ResponseStatusException(
						HttpStatus.NOT_FOUND, "Screening id not found: "+screeningId));
		return ticketService.createNTicketsWithPessimisticLock(screening, numberOfTickets).stream()
				.<TicketDto>map(ticket -> MappedDto.convertToDto(ticket, TicketDto.class,  modelMapper))
				.toList()
				;
	}

	@Operation(summary = "Creates the given number of Tickets for the Screening with some available Seats optimistically.")
	@ApiResponses(value = { 
	  @ApiResponse(responseCode = "201", description = "Successfully created all Tickets.", 
	    content = { @Content(mediaType = "application/json", 
	    array = @ArraySchema(schema = @Schema(implementation = TicketDto.class))
	    )}),
	  @ApiResponse(responseCode = "400", description = "Invalid number of Tickets supplied or there are not enough available Seats.", content = @Content), 
	  @ApiResponse(responseCode = "404", description = "The given Screening not found.", content = @Content),
	  @ApiResponse(responseCode = "409", description = "Could not create the tickets optimistically.", content = @Content) 
	  })
	@PostMapping("/booknretry")
	@ResponseStatus(HttpStatus.CREATED)
	List<TicketDto> createNTicketsWithRetry(
			@Parameter(description = "ID of Screening we are to create Tickets for.") 
			@RequestParam("screeningId") Long screeningId,
			@Parameter(description = "Number of Ticket to be created.") 
			@RequestParam("numberOfTickets") @Min(1) Integer numberOfTickets
	) {
		Screening screening = screeningService.getById(screeningId)
				.orElseThrow(() -> new ResponseStatusException(
						HttpStatus.NOT_FOUND, "Screening id not found: "+screeningId));
		return ticketService.createNTicketsWithRetryTemplate(screening, numberOfTickets).stream()
				.<TicketDto>map(ticket -> MappedDto.convertToDto(ticket, TicketDto.class,  modelMapper))
				.toList()
				;
	}

}
