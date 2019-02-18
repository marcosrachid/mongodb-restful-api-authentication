package com.up.handler;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import com.mongo.domain.dto.UserDTO;
import com.mongo.domain.dto.request.UserPasswordDTORequest;
import com.up.service.exception.BusinessException;

public interface UserHandler {

	UserDTO createUser(UserPasswordDTORequest userPasswordDTORequest, OAuth2Authentication auth) throws BusinessException;

	Optional<UserDTO> updateUser(UserPasswordDTORequest userPasswordDTORequest, OAuth2Authentication auth) throws BusinessException;

	Optional<UserDTO> deleteUser(String login, OAuth2Authentication auth) throws BusinessException;

	Page<UserDTO> getAllUsers(Pageable pageable);

	Optional<UserDTO> getUserByUsername(String login, OAuth2Authentication auth) throws BusinessException;

	List<LocalDate> getReservationSchedule(String login, OAuth2Authentication auth) throws BusinessException;

}
