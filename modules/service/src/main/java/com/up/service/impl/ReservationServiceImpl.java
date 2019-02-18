package com.up.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.google.common.collect.Sets;
import com.mongo.domain.Reservation;
import com.mongo.domain.repository.ReservationRepository;
import com.up.service.ReservationService;
import com.up.service.exception.BusinessException;

/**
 * Reservations service layer
 * 
 * @author Marcos Rachid
 *
 */
@Service
public class ReservationServiceImpl implements ReservationService {

	private static final Logger LOG = LoggerFactory.getLogger(ReservationServiceImpl.class);

	@Value("${application.min.reservation:1}")
	private Long minDayFromCurrentAbleToReserve; // 1 day default

	@Value("${application.max.reservation:30}")
	private Long maxDayFromCurrentAbleToReserve; // 1 month default

	@Value("${application.max.days:3}")
	private Integer maxReservationDaysPerUser; // 3 days default

	private ReservationRepository reservationRepository;

	public ReservationServiceImpl(ReservationRepository reservationRepository) {
		this.reservationRepository = reservationRepository;
	}

	@Override
	public Page<LocalDate> getAvailability(LocalDate startDate, LocalDate endDate, Pageable pageable) {
		LOG.debug("Search Availability from {} to {}", startDate, endDate);
		return reservationRepository
				.findByLoginNullAndReservationDateBetween(startDate.minusDays(1), endDate.plusDays(1), pageable)
				.map(Reservation::getReservationDate);
	}

	@Override
	public synchronized List<LocalDate> reserve(LocalDate startDate, LocalDate endDate, String login) throws BusinessException {
		LOG.debug("Submiting a reserve from {} to {} for user {} ", startDate, endDate, login);
		long period = ChronoUnit.DAYS.between(startDate, endDate) + 1;
		if (period > maxReservationDaysPerUser)
			throw new BusinessException("Period cannot be of more then " + maxReservationDaysPerUser + " days");

		// Searching on reservable period if there is any reservation for specified login
		List<LocalDate> dates = Stream.iterate(startDate, date -> date.plusDays(1)).limit(period)
				.collect(Collectors.toList());
		LocalDate startingAbleDay = LocalDate.now().plusDays(minDayFromCurrentAbleToReserve);
		LocalDate lastingAbleDay = LocalDate.now().plusDays(maxDayFromCurrentAbleToReserve);
		List<Reservation> loginReservations = reservationRepository.findByLoginAndReservationDateBetween(login,
				startingAbleDay.minusDays(1), lastingAbleDay.plusDays(1));
		List<LocalDate> reservationDates = loginReservations.stream().map(Reservation::getReservationDate)
				.collect(Collectors.toList());
		dates.addAll(reservationDates);

		// Check if login already has max days per user on reservable period
		if (Sets.newHashSet(dates).size() > maxReservationDaysPerUser)
			throw new BusinessException("It's not possible to have more then " + maxReservationDaysPerUser
					+ " days reserved between " + startingAbleDay.format(DateTimeFormatter.ISO_DATE) + " and "
					+ lastingAbleDay.format(DateTimeFormatter.ISO_DATE));

		// Check if the period is already reserved
		List<Reservation> reservations = reservationRepository.findByReservationDateBetween(startDate.minusDays(1),
				endDate.plusDays(1));
		if (reservations.stream().filter(r -> r.getLogin() != null).count() > 0)
			throw new BusinessException(
					"There are unavailable days between " + startDate.format(DateTimeFormatter.ISO_DATE) + " and "
							+ endDate.format(DateTimeFormatter.ISO_DATE));

		reservations.forEach(r -> {
			r.setLogin(login);
			r.setSubmitReservationDateTime(LocalDateTime.now());
		});

		return reservationRepository.saveAll(reservations).stream().map(Reservation::getReservationDate)
				.collect(Collectors.toList());
	}

	@Override
	public List<LocalDate> cancelReservation(LocalDate startDate, LocalDate endDate, String login) {
		LOG.debug("Submiting a cancel from {} to {} for user {} ", startDate, endDate, login);
		List<Reservation> reservations = reservationRepository.findByLoginAndReservationDateBetween(login,
				startDate.minusDays(1), endDate.plusDays(1));
		reservations.forEach(r -> {
			r.setLogin(null);
			r.setSubmitReservationDateTime(null);
		});
		return reservationRepository.saveAll(reservations).stream().map(Reservation::getReservationDate)
				.collect(Collectors.toList());
	}

}
