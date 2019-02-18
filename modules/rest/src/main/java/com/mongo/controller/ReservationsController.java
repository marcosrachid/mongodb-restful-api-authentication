package com.mongo.controller;

import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mongo.domain.dto.ResponseDTO;
import com.up.handler.ReservationHandler;
import com.up.utils.UserUtils;

@RestController
@RequestMapping("/api")
public class ReservationsController {

	private static final Logger LOG = LoggerFactory.getLogger(ReservationsController.class);

	private ReservationHandler reservationsHandler;

	public ReservationsController(ReservationHandler reservationsHandler) {
		this.reservationsHandler = reservationsHandler;
	}

	/**
	 * GET: /reservations?page=?&startDate=?&endDate=?: get availabilities from
	 * specified period
	 * 
	 * @param pageable
	 * @param startDate
	 * @param endDate
	 * @return
	 * @throws Exception
	 */
	@GetMapping("/v1/reservations")
	public ResponseEntity<ResponseDTO> getAvailabilityByPeriod(Pageable pageable,
			@RequestParam(name = "startDate", required = false) @DateTimeFormat(iso = ISO.DATE) LocalDate startDate,
			@RequestParam(name = "endDate", required = false) @DateTimeFormat(iso = ISO.DATE) LocalDate endDate)
			throws Exception {
		LOG.debug("REST get Availability from {} to {}", startDate, endDate);
		return ResponseEntity.status(HttpStatus.OK)
				.body(new ResponseDTO(reservationsHandler.getAvailability(startDate, endDate, pageable).getContent()));
	}

	/**
	 * POST: /reservations: reserve a reservation date for current user
	 * 
	 * @param reservationDate
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/v1/reservations")
	public ResponseEntity<ResponseDTO> reserve(
			@RequestParam(name = "startDate", required = false) @DateTimeFormat(iso = ISO.DATE) LocalDate startDate,
			@RequestParam(name = "endDate", required = false) @DateTimeFormat(iso = ISO.DATE) LocalDate endDate,
			OAuth2Authentication auth) throws Exception {
		LOG.debug("REST request to reserve from {} to {}", startDate, endDate);
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(new ResponseDTO(reservationsHandler.reserve(startDate, endDate, auth)));
	}

	/**
	 * DELETE: /reservations: cancel a reservation date for current user
	 * 
	 * @param date
	 * @return
	 * @throws Exception
	 */
	@DeleteMapping("/v1/reservations/{login:" + UserUtils.LOGIN_REGEX + "}")
	public ResponseEntity<ResponseDTO> cancelReserve(@PathVariable String login,
			@RequestParam(name = "startDate", required = false) @DateTimeFormat(iso = ISO.DATE) LocalDate startDate,
			@RequestParam(name = "endDate", required = false) @DateTimeFormat(iso = ISO.DATE) LocalDate endDate,
			OAuth2Authentication auth) throws Exception {
		LOG.debug("REST request to cancel reserve from {} to {}", startDate, endDate);
		return ResponseEntity.status(HttpStatus.ACCEPTED)
				.body(new ResponseDTO(reservationsHandler.cancelReservation(login, startDate, endDate, auth)));
	}

}