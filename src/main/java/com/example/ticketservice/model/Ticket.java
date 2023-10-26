package com.example.ticketservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * The Ticket class represents a ticket for a certain Screening and Seat. 
 * No two Tickets can exists for the same Screening and Seat.
 */
@Data @NoArgsConstructor(force = true) @RequiredArgsConstructor
@EqualsAndHashCode(of = {"id"}) @ToString(includeFieldNames=false, of = {"screening", "seat"})
@Entity
@Table(uniqueConstraints = { @UniqueConstraint(name = "UniqueScreeningAndSeat", columnNames = { "SCREENING_ID", "SEAT_ID" }) })
public class Ticket {
	@Id
	@GeneratedValue
	@Column(name = "TICKET_ID")
	Long id;

	@ManyToOne(optional = false)
	@JoinColumn(name="SCREENING_ID")
	final Screening screening;

	@ManyToOne(optional = false)
	@JoinColumn(name="SEAT_ID")
	final Seat seat;
}
