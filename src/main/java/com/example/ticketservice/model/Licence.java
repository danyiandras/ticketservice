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
 * The Licence class represents the time interval a Theater is allowed to show a Movie.
 */
@Data @NoArgsConstructor(force = true) @RequiredArgsConstructor
@EqualsAndHashCode(of = {"id"}) @ToString(includeFieldNames=false, of = {"theater", "movie"})
@Entity
public class Licence {
	@Id
	@GeneratedValue
	@Column(name = "LICENCE_ID")
	Long id;

	@ManyToOne(optional = false)
	@JoinColumn(name="THEATER_ID")
	final Theater theater;

	@ManyToOne(optional = false)
	@JoinColumn(name="MOVIE_ID")
	final Movie movie;

	@TimeZoneStorage(TimeZoneStorageType.NORMALIZE_UTC)
	@Column(name = "START_TIME", nullable = false)
	final ZonedDateTime startDate;
	@TimeZoneStorage(TimeZoneStorageType.NORMALIZE_UTC)
	@Column(name = "END_TIME", nullable = false)
	final ZonedDateTime endDate;
}
