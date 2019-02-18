package com.up.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.mongo.domain.Reservation;
import com.mongo.domain.User;
import com.mongo.domain.dto.UserDTO;
import com.mongo.domain.dto.request.UserPasswordDTORequest;
import com.mongo.domain.repository.ReservationRepository;
import com.mongo.domain.repository.UserRepository;
import com.up.service.UserService;

/**
 * User service layer
 * 
 * @author Marcos Rachid
 *
 */
@Service
public class UserServiceImpl implements UserService {

	private static final Logger LOG = LoggerFactory.getLogger(UserServiceImpl.class);

	private UserRepository userRepository;

	private ReservationRepository reservationRepository;
	
	private PasswordEncoder passwordEncoder;

	public UserServiceImpl(UserRepository userRepository, ReservationRepository reservationRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.reservationRepository = reservationRepository;
		this.passwordEncoder = passwordEncoder;
	}

	/**
	 * 
	 * @param UserDTO
	 * @return
	 */
	public UserDTO createUser(UserPasswordDTORequest userDTO) {
		User newUser = new User();
		newUser.setUsername(userDTO.getUsername());
		newUser.setEmail(userDTO.getEmail());
		newUser.setName(userDTO.getName());
		newUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));
		newUser.setRoles(userDTO.getRoles());
		userRepository.save(newUser);
		LOG.debug("Created Information for User: {}", userDTO);
		userDTO.setPassword(null);
		return userDTO;
	}

	/**
	 * Update basic information (first name, last name, email, language) for the
	 * current user.
	 *
	 * @param userDTO return
	 */
	public Optional<UserDTO> updateUser(UserPasswordDTORequest userDTO) {
		return userRepository.findOneByUsername(userDTO.getUsername()).map(user -> {
			if (StringUtils.isNotEmpty(userDTO.getEmail()))
				user.setEmail(userDTO.getEmail());
			if (StringUtils.isNotEmpty(userDTO.getPassword()))
				user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
			if (userDTO.getRoles() != null && !userDTO.getRoles().isEmpty())
				user.setRoles(userDTO.getRoles());
			user.setModifiedDate(LocalDateTime.now());
			userRepository.save(user);
			LOG.debug("Changed Information for User: {}", user);
			return user;
		}).map(UserDTO::new);
	}

	/**
	 * 
	 * @param login
	 */
	public Optional<UserDTO> deleteUser(String login) {
		return userRepository.findOneByUsername(login).map(user -> {
			userRepository.delete(user);
			LOG.debug("Deleted User: {}", user);
			return user;
		}).map(UserDTO::new);
	}

	/**
	 * return
	 */
	@Override
	public Page<UserDTO> getAllUsers(Pageable pageable) {
		LOG.debug("Search All Users");
		return userRepository.findAll(pageable).map(UserDTO::new);
	}

	/**
	 * @param email return
	 */
	@Override
	public Optional<UserDTO> getUserByUsername(String login) {
		LOG.debug("Search User: {}", login);
		return userRepository.findOneByUsername(login).map(UserDTO::new);
	}

	/**
	 * @param login
	 * return
	 */
	@Override
	public List<LocalDate> getReservationSchedule(String login) {
		return reservationRepository.findByLogin(login).stream().map(Reservation::getReservationDate)
				.collect(Collectors.toList());
	}

}
