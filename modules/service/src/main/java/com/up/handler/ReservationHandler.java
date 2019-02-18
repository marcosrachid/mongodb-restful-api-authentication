package com.up.handler;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import com.up.service.exception.BusinessException;

public interface ReservationHandler {

	Page<LocalDate> getAvailability(LocalDate startDate, LocalDate endDate, Pageable pageable) throws BusinessException;

	List<LocalDate> reserve(LocalDate startDate, LocalDate endDate, OAuth2Authentication auth) throws BusinessException;

	List<LocalDate> cancelReservation(String login, LocalDate startDate, LocalDate endDate, OAuth2Authentication auth)
			throws BusinessException;

}
