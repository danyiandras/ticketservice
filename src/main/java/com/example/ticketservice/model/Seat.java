package com.example.ticketservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * The Seat class represents a seat in a Theater. Every Seat has a unique name in a Theater.
 */
@Data @NoArgsConstructor(force = true) @RequiredArgsConstructor
@EqualsAndHashCode(of = {"id"}) @ToString(includeFieldNames=false, of = {"theater", "name"})
@Entity
@Table(uniqueConstraints = {@UniqueConstraint(name = "", columnNames = {"THEATER_ID", "NAME"})})
public class Seat {
	@Id
	@GeneratedValue
	@Column(name = "SEAT_ID")
	Long id;
	
	@Size(min=1, max=256)
	@Column(nullable = false)
	final String name;

	@ManyToOne(optional = false)
	@JoinColumn(name = "THEATER_ID")
	final Theater theater;	
}
