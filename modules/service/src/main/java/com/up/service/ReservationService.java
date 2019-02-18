package com.up.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.up.service.exception.BusinessException;

public interface ReservationService {

	Page<LocalDate> getAvailability(LocalDate startDate, LocalDate endDate, Pageable pageable);

	List<LocalDate> reserve(LocalDate startDate, LocalDate endDate, String login) throws BusinessException;

	List<LocalDate> cancelReservation(LocalDate startDate, LocalDate endDate, String login);

}
