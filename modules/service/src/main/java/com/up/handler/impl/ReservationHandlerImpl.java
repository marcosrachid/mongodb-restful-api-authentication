package com.up.handler.impl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.up.handler.ReservationHandler;
import com.up.service.ReservationService;
import com.up.service.exception.BusinessException;
import com.up.utils.UserUtils;

@Component
@Transactional
public class ReservationHandlerImpl implements ReservationHandler {

	@Value("${application.min.reservation:1}")
	private Long minDayFromCurrentAbleToReserve; // 1 day default

	@Value("${application.max.reservation:30}")
	private Long maxDayFromCurrentAbleToReserve; // 1 month default

	private ReservationService reservationsService;

	public ReservationHandlerImpl(ReservationService reservationsService) {
		this.reservationsService = reservationsService;
	}

	@Override
	public Page<LocalDate> getAvailability(LocalDate startDate, LocalDate endDate, Pageable pageable)
			throws BusinessException {
		if (startDate == null || endDate == null) {
			LocalDate currentDate = LocalDate.now();
			return reservationsService.getAvailability(currentDate.plusDays(minDayFromCurrentAbleToReserve),
					currentDate.plusDays(maxDayFromCurrentAbleToReserve), pageable);
		} else {
			checkReservationDates(startDate, endDate);
			return reservationsService.getAvailability(startDate, endDate, pageable);
		}
	}

	@Override
	public List<LocalDate> reserve(LocalDate startDate, LocalDate endDate, OAuth2Authentication auth)
			throws BusinessException {
		checkReservationDates(startDate, endDate);
		return reservationsService.reserve(startDate, endDate, ((UserDetails) auth.getPrincipal()).getUsername());
	}

	@Override
	public List<LocalDate> cancelReservation(String login, LocalDate startDate, LocalDate endDate,
			OAuth2Authentication auth) throws BusinessException {
		UserUtils.isAdminOrCurrentUser(auth, login);
		checkCancelReservationDates(startDate, endDate);
		return reservationsService.cancelReservation(startDate, endDate,
				((UserDetails) auth.getPrincipal()).getUsername());
	}

	/**
	 * Check if start date is after end date
	 * 
	 * @param startDate
	 * @param endDate
	 * @throws BusinessException
	 */
	private void checkDates(LocalDate startDate, LocalDate endDate) throws BusinessException {
		if (endDate.isBefore(startDate))
			throw new BusinessException("The start date cannot be before end date");
	}

	/**
	 * Check if the reservation dates are between the available dates
	 * 
	 * @param startDate
	 * @param endDate
	 * @throws BusinessException
	 */
	private void checkReservationDates(LocalDate startDate, LocalDate endDate) throws BusinessException {
		checkDates(startDate, endDate);
		LocalDate startingAbleDay = LocalDate.now().plusDays(minDayFromCurrentAbleToReserve);
		LocalDate lastingAbleDay = LocalDate.now().plusDays(maxDayFromCurrentAbleToReserve);
		if (startDate.isBefore(startingAbleDay))
			throw new BusinessException(
					"The start date cannot be before " + startingAbleDay.format(DateTimeFormatter.ISO_DATE));
		if (endDate.isAfter(lastingAbleDay))
			throw new BusinessException(
					"The end date cannot be after " + lastingAbleDay.format(DateTimeFormatter.ISO_DATE));
	}

	/**
	 * Check if is not canceling a past reservation
	 * 
	 * @param startDate
	 * @param endDate
	 * @throws BusinessException
	 */
	private void checkCancelReservationDates(LocalDate startDate, LocalDate endDate) throws BusinessException {
		checkDates(startDate, endDate);
		if (startDate.isBefore(LocalDate.now()))
			throw new BusinessException("It's now possible to cancel a past reservation");
	}

}
