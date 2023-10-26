package com.example.ticketservice.controller.dto;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class TicketOrderDto {
	
	@NotNull
	Long screeningId;
	
	@NotEmpty
	List<Long> seatIds;

}
