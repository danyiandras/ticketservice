package com.example.ticketservice;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Primary;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ConfigurationProperties(prefix = "ticketservice")
@ConfigurationPropertiesScan
@Getter @Setter @ToString
@Primary
public class TicketServiceApplicationConfigurationProperties {

	@Min(1)
	private int createNTicketRetry = 1;
	
}
