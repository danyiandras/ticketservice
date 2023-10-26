package com.example.ticketservice;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.example.ticketservice.model.Licence;
import com.example.ticketservice.model.Movie;
import com.example.ticketservice.model.Screening;
import com.example.ticketservice.model.Seat;
import com.example.ticketservice.model.Theater;
import com.example.ticketservice.repository.LicenceRepository;
import com.example.ticketservice.repository.MovieRepository;
import com.example.ticketservice.repository.ScreeningRepository;
import com.example.ticketservice.repository.SeatRepository;
import com.example.ticketservice.repository.TheaterRepository;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class TicketServiceApplicationTestDataLoader implements ApplicationRunner {

	private final MovieRepository movieRepository;
	private final TheaterRepository theaterRepository;
	private final LicenceRepository licenceRepository;
	private final ScreeningRepository screeningRepository;
	private final SeatRepository seatRepository;
	
	public TicketServiceApplicationTestDataLoader(
			MovieRepository movieRepository, 
			TheaterRepository theaterRepository, 
			LicenceRepository licenceRepository,
			ScreeningRepository screeningRepository, 
			SeatRepository seatRepository
	){
		this.movieRepository = movieRepository;
		this.theaterRepository = theaterRepository;
		this.screeningRepository = screeningRepository;
		this.licenceRepository = licenceRepository;
		this.seatRepository = seatRepository;
	}
	

	@Override
	public void run(ApplicationArguments args) throws Exception {
		log.info("Init Movies.");
		Movie theGodFather = movieRepository.save(new Movie("The Godfather"));
		Movie theDeerHunter = movieRepository.save(new Movie("The Deer Hunter"));
		Movie ragingBull = movieRepository.save(new Movie("Raging Bull"));
		Movie taxiDriver = movieRepository.save(new Movie("Taxi Driver"));
		Movie casino = movieRepository.save(new Movie("Casino"));
		Movie theIntern = movieRepository.save(new Movie("The Intern"));
		
		Map<Movie, Integer> movieStarts = Map.of(
				theGodFather, 16,
				theDeerHunter, 19,
				ragingBull, 21,
				taxiDriver, 16,
				casino, 19,
				theIntern, 21
				);

		log.info("Init Theaters.");
		Theater one = theaterRepository.save(new Theater("Theater One"));
		Theater two = theaterRepository.save(new Theater("Theater Two"));
		Theater three = theaterRepository.save(new Theater("Theater Three"));
		Theater four = theaterRepository.save(new Theater("Theater Four"));
		
		log.info("Init Licences.");
		Stream.of(one, two).forEach(theater -> {
			Stream.of(theGodFather, theDeerHunter, ragingBull).forEach(movie -> {
				licenceRepository.save(new Licence(theater, movie, ZonedDateTime.now(), ZonedDateTime.now().plusMonths(2)));
			});
			Stream.of(taxiDriver, casino, theIntern).forEach(movie -> {
				licenceRepository.save(new Licence(theater, movie, ZonedDateTime.now().plusMonths(2), ZonedDateTime.now().plusMonths(4)));
			});
		});
		Stream.of(three, four).forEach(theater -> {
			Stream.of(theGodFather, theDeerHunter, ragingBull).forEach(movie -> {
				licenceRepository.save(new Licence(theater, movie, ZonedDateTime.now().plusMonths(2), ZonedDateTime.now().plusMonths(4)));
			});
			Stream.of(taxiDriver, casino, theIntern).forEach(movie -> {
				licenceRepository.save(new Licence(theater, movie, ZonedDateTime.now(), ZonedDateTime.now().plusMonths(2)));
			});
		});

		log.info("Init Seats.");
		theaterRepository.findAll().stream()
			.forEach(theater -> {
				IntStream.range(0, 20).forEach(i -> {
					seatRepository.save(new Seat("Seat %d".formatted(i), theater));
				});
			});
		
		log.info("Init Screenings.");
		theaterRepository.findAll().stream()
			.forEach(theater -> {
				licenceRepository.findByTheater(theater).stream().forEach(licence -> {
					licence.getStartDate().toLocalDate().datesUntil(licence.getEndDate().toLocalDate()).forEach(date -> {
						ZonedDateTime movieStart = date.atTime(movieStarts.get(licence.getMovie()), 0).atZone(ZoneId.systemDefault());
						screeningRepository.save(new Screening(licence.getMovie(), theater, movieStart, movieStart.plusHours(3)));
					});
				});
			});
	}

}
