package com.example.ticketservice.model;

import java.time.ZonedDateTime;

import org.hibernate.annotations.TimeZoneStorage;
import org.hibernate.annotations.TimeZoneStorageType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * The Screening class represents a show of a Movie in a Theater in a certain time interval.
 * Two Screenings in a Theater can not overlap in time.
 */
@Data @NoArgsConstructor(force = true) @RequiredArgsConstructor
@EqualsAndHashCode(of = {"id"}) @ToString(includeFieldNames=false, of = {"theater", "movie", "startTime", "endTime"})
@Entity
public class Screening {
	@Id
	@GeneratedValue
	@Column(name = "SCREENING_ID")
	Long id;

	@ManyToOne(optional = false)
	@JoinColumn(name="MOVIE_ID")
	final Movie movie;

	@ManyToOne(optional = false)
	@JoinColumn(name="THEATER_ID")
	final Theater theater;

	@TimeZoneStorage(TimeZoneStorageType.NORMALIZE_UTC)
	@Column(name = "START_TIME", nullable = false)
	final ZonedDateTime startTime;
	@TimeZoneStorage(TimeZoneStorageType.NORMALIZE_UTC)
	@Column(name = "END_TIME", nullable = false)
	final ZonedDateTime endTime;
}
