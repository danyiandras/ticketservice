package com.example.ticketservice;

import java.time.ZonedDateTime;

public class TestConstants {
	public static ZonedDateTime searchStart = ZonedDateTime.now().plusDays(1).withHour(14).withMinute(0);
	public static ZonedDateTime searchEnd = searchStart.plusHours(5);
}
