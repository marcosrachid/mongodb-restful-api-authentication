package com.up.handler.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.mongo.domain.constants.Role;
import com.mongo.domain.dto.UserDTO;
import com.mongo.domain.dto.request.UserPasswordDTORequest;
import com.up.handler.UserHandler;
import com.up.service.UserService;
import com.up.service.exception.BusinessException;
import com.up.utils.UserUtils;

@Component
@Transactional
public class UserHandlerImpl implements UserHandler {

	private UserService userService;

	public UserHandlerImpl(UserService userService) {
		this.userService = userService;
	}

	@Override
	public UserDTO createUser(UserPasswordDTORequest userPasswordDTORequest, OAuth2Authentication auth)
			throws BusinessException {
		if (!userPasswordDTORequest.getRoles().contains(Role.ADMIN)
				&& !userPasswordDTORequest.getRoles().contains(Role.USER))
			throw new BusinessException("Specified role must be: " + Role.set());
		if (userPasswordDTORequest.getRoles().contains(Role.ADMIN) && auth != null
				&& !auth.getAuthorities().contains(new SimpleGrantedAuthority(Role.ADMIN)))
			throw new BusinessException("Must be an administrator to create an administrator user");
		return userService.createUser(userPasswordDTORequest);
	}

	@Override
	public Optional<UserDTO> updateUser(UserPasswordDTORequest userPasswordDTORequest, OAuth2Authentication auth)
			throws BusinessException {
		UserUtils.isAdminOrCurrentUser(auth, userPasswordDTORequest.getUsername());
		return userService.updateUser(userPasswordDTORequest);
	}

	@Override
	public Optional<UserDTO> deleteUser(String login, OAuth2Authentication auth) throws BusinessException {
		UserUtils.isAdminOrCurrentUser(auth, login);
		return userService.deleteUser(login);
	}

	@Override
	public Page<UserDTO> getAllUsers(Pageable pageable) {
		return userService.getAllUsers(pageable);
	}

	@Override
	public Optional<UserDTO> getUserByUsername(String login, OAuth2Authentication auth) throws BusinessException {
		UserUtils.isAdminOrCurrentUser(auth, login);
		return userService.getUserByUsername(login);
	}

	@Override
	public List<LocalDate> getReservationSchedule(String login, OAuth2Authentication auth) throws BusinessException {
		UserUtils.isAdminOrCurrentUser(auth, login);
		return userService.getReservationSchedule(login);
	}

}
