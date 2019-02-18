package com.mongo.domain;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "reservation")
public class Reservation implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	private String id;

	@Indexed(unique = true)
	private LocalDate reservationDate;

	private String login;

	private LocalDateTime submitReservationDateTime;

	public Reservation() {
	}

	public Reservation(LocalDate reservationDate) {
		super();
		this.reservationDate = reservationDate;
	}

	public Reservation(LocalDate reservationDate, String login) {
		super();
		this.reservationDate = reservationDate;
		this.login = login;
		this.submitReservationDateTime = LocalDateTime.now();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public LocalDate getReservationDate() {
		return reservationDate;
	}

	public void setReservationDate(LocalDate reservationDate) {
		this.reservationDate = reservationDate;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public LocalDateTime getSubmitReservationDateTime() {
		return submitReservationDateTime;
	}

	public void setSubmitReservationDateTime(LocalDateTime submitReservationDateTime) {
		this.submitReservationDateTime = submitReservationDateTime;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((reservationDate == null) ? 0 : reservationDate.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Reservation other = (Reservation) obj;
		if (reservationDate == null) {
			if (other.reservationDate != null)
				return false;
		} else if (!reservationDate.equals(other.reservationDate))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Reservation [id=" + id + ", reservationDate=" + reservationDate + ", login=" + login
				+ ", submitReservationDateTime=" + submitReservationDateTime + "]";
	}

}
