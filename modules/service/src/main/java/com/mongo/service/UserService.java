package com.up.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.mongo.domain.dto.UserDTO;
import com.mongo.domain.dto.request.UserPasswordDTORequest;

public interface UserService {
	
	UserDTO createUser(UserPasswordDTORequest userDTO);
	
	Optional<UserDTO> updateUser(UserPasswordDTORequest userDTO);
	
	Optional<UserDTO> deleteUser(String login);
	
	Page<UserDTO> getAllUsers(Pageable pageable);

	Optional<UserDTO> getUserByUsername(String login);

	List<LocalDate> getReservationSchedule(String login);

}
