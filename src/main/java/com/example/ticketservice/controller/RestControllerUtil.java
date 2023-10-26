package com.example.ticketservice.controller;

import java.time.ZonedDateTime;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RestControllerUtil {

	public static void validateTimeIntervall(ZonedDateTime startTime, ZonedDateTime endTime) {
		if (endTime.isEqual(startTime) || startTime.isAfter(endTime)) {
			log.debug("Not a valid time interval: ({}, {})", startTime, endTime);
			throw new IllegalArgumentException();
		}
	}

}
