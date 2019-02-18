package com.mongo.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;

import java.net.URISyntaxException;
import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mongo.domain.dto.ResponseDTO;
import com.mongo.domain.dto.UserDTO;
import com.mongo.domain.dto.request.UserPasswordDTORequest;
import com.up.handler.UserHandler;
import com.up.service.exception.BusinessException;
import com.up.utils.UserUtils;

@RestController
@RequestMapping("/api")
public class UserController {

	private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

	private UserHandler userHandler;

	public UserController(UserHandler userHandler) {
		this.userHandler = userHandler;
	}

	/**
	 * POST /v1/users : Creates a new user.
	 * <p>
	 * Creates a new user if the login and email are not already used, and sends an
	 * mail with an activation link. The user needs to be activated on creation.
	 * </p>
	 *
	 * @param userPasswordDTORequest the user to create
	 * @return the ResponseEntity with status 201 (Created) and with body the new
	 *         user, or with status 400 (Bad Request) if the login or email is
	 *         already in use
	 * @throws URISyntaxException if the Location URI syntax is incorrect
	 * @throws BusinessException
	 */
	@PostMapping("/v1/users")
	public ResponseEntity<ResponseDTO> createUser(@Valid @RequestBody UserPasswordDTORequest userPasswordDTORequest,
			OAuth2Authentication auth) throws Exception {
		LOG.debug("REST request to save User : {}", userPasswordDTORequest);
		UserDTO newUser = userHandler.createUser(userPasswordDTORequest, auth);
		return ResponseEntity.status(HttpStatus.CREATED).contentType(APPLICATION_JSON_UTF8)
				.body(new ResponseDTO(newUser));
	}

	/**
	 * PUT /v1/users : Updates an existing User.
	 *
	 * @param userPasswordDTORequest the user to update
	 * @return the ResponseEntity with status 200 (OK) and with body the updated
	 *         user, or with status 400 (Bad Request) if the login or email is
	 *         already in use, or with status 500 (Internal Server Error) if the
	 *         user couldn't be updated
	 */
	@PutMapping("/v1/users")
	public ResponseEntity<ResponseDTO> updateUser(@Valid @RequestBody UserPasswordDTORequest userPasswordDTORequest,
			OAuth2Authentication auth) throws Exception {
		LOG.debug("REST request to update User : {}", userPasswordDTORequest);
		Optional<UserDTO> userDTO = userHandler.updateUser(userPasswordDTORequest, auth);
		if (userDTO.isPresent())
			return ResponseEntity.status(HttpStatus.ACCEPTED).contentType(APPLICATION_JSON_UTF8)
					.body(new ResponseDTO(userDTO.get()));
		return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	}

	/**
	 * GET /v1/users : get all users.
	 *
	 * @param pageable the pagination information
	 * @return the ResponseEntity with status 200 (OK) and with bosdy all users
	 */
	@GetMapping("/v1/users")
	public ResponseEntity<ResponseDTO> getAllUsers(Pageable pageable) {
		final Page<UserDTO> page = userHandler.getAllUsers(pageable);
		return ResponseEntity.status(HttpStatus.OK).body(new ResponseDTO(page.getContent()));
	}

	/**
	 * GET /v1/users/:login : get the "login" user.
	 *
	 * @param login the login of the user to find
	 * @return the ResponseEntity with status 200 (OK) and with body the "login"
	 *         user, or with status 404 (Not Found)
	 */
	@GetMapping("/v1/users/{login:" + UserUtils.LOGIN_REGEX + "}")
	public ResponseEntity<ResponseDTO> getUser(@PathVariable String login, OAuth2Authentication auth) throws Exception {
		LOG.debug("REST request to get User : {}", login);
		Optional<UserDTO> userDTO = userHandler.getUserByUsername(login, auth);
		if (userDTO.isPresent())
			return ResponseEntity.status(HttpStatus.OK).contentType(APPLICATION_JSON_UTF8)
					.body(new ResponseDTO(userDTO.get()));
		return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	}

	/**
	 * DELETE /v1/users/:login : delete the "login" User.
	 *
	 * @param login the login of the user to delete
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@DeleteMapping("/v1/users/{login:" + UserUtils.LOGIN_REGEX + "}")
	public ResponseEntity<ResponseDTO> deleteUser(@PathVariable String login, OAuth2Authentication auth)
			throws Exception {
		LOG.debug("REST request to delete User: {}", login);
		Optional<UserDTO> userDTO = userHandler.deleteUser(login, auth);
		if (userDTO.isPresent())
			return ResponseEntity.status(HttpStatus.OK).contentType(APPLICATION_JSON_UTF8)
					.body(new ResponseDTO(userDTO.get()));
		return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	}

	/**
	 * GET /v1/users/:login/reservations: get users's reservations
	 * 
	 * @param login
	 * @param auth
	 * @return
	 * @throws Exception
	 */
	@GetMapping("/v1/users/{login:" + UserUtils.LOGIN_REGEX + "}/reservations")
	public ResponseEntity<ResponseDTO> getReservationSchedule(@PathVariable String login, OAuth2Authentication auth)
			throws Exception {
		LOG.debug("REST request to get user {} reservation schedule", login);
		return ResponseEntity.status(HttpStatus.OK)
				.body(new ResponseDTO(userHandler.getReservationSchedule(login, auth)));
	}
}
