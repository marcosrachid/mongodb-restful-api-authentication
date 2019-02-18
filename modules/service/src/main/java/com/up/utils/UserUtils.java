package com.up.utils;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import com.mongo.domain.constants.Role;
import com.up.service.exception.BusinessException;

public class UserUtils {
	
	public static final String LOGIN_REGEX = "^[_'.@A-Za-z0-9-]*$";

	public static void isAdminOrCurrentUser(OAuth2Authentication auth, String username) throws BusinessException {
		if (!auth.getAuthorities().contains(new SimpleGrantedAuthority(Role.ADMIN))
				&& !((UserDetails) auth.getPrincipal()).getUsername().equals(username))
			throw new BusinessException("Current user does not have the authorities to perform this task");
	}

}
