package com.example.ticketservice;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TicketServiceApplicationConfiguration {

    @Bean
    ModelMapper modelMapper() {
	    return new ModelMapper();
	}
}
