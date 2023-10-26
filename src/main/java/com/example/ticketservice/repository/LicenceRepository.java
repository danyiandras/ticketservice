package com.example.ticketservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ticketservice.model.Licence;
import com.example.ticketservice.model.Theater;

public interface LicenceRepository extends JpaRepository<Licence, Long> {

	List<Licence> findByTheater(Theater theater);

}
