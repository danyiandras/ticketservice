package com.example.ticketservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * The Movie class represents a movie with a unique title.
 */
@Data @NoArgsConstructor(force = true) @RequiredArgsConstructor
@EqualsAndHashCode(of = {"id"}) @ToString(includeFieldNames=false, of = {"title"})
@Entity
public class Movie {

	@Id
	@GeneratedValue
	@Column(name = "MOVIE_ID")
	Long id;
	
	@Size(min=1, max=256)
	@Column(unique=true, nullable = false)
	final String title;
}
