package com.mongo.domain.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.mongo.domain.Reservation;

public interface ReservationRepository extends MongoRepository<Reservation, String> {

	List<Reservation> findByReservationDateBetween(LocalDate startDate, LocalDate endDate);

	Page<Reservation> findByLoginNullAndReservationDateBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);

	List<Reservation> findByLoginAndReservationDateBetween(String login, LocalDate startDate, LocalDate endDate);

	List<Reservation> findByLogin(String login);

}
