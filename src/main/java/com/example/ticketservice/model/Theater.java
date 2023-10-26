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
 * The Theater class represents a movie theater with only one auditorium. Every Theater has a unique name.
 */
@Data @NoArgsConstructor(force = true) @RequiredArgsConstructor
@EqualsAndHashCode(of = {"id"}) @ToString(includeFieldNames=false, of = {"name"})
@Entity
public class Theater {
	@Id
	@GeneratedValue
	@Column(name = "THEATER_ID")
	Long id;
	
	@Size(min=1, max=256)
	@Column(unique=true, nullable = false)
	final String name;
}
