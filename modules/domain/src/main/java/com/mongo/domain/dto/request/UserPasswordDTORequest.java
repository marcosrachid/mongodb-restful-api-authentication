package com.mongo.domain.dto.request;

import java.util.Set;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.mongo.domain.dto.UserDTO;

public class UserPasswordDTORequest extends UserDTO {

	private static final long serialVersionUID = 1L;

	public static final int PASSWORD_MIN_LENGTH = 4;

	public static final int PASSWORD_MAX_LENGTH = 100;

	@NotNull
	@Size(min = PASSWORD_MIN_LENGTH, max = PASSWORD_MAX_LENGTH)
	private String password;

	public UserPasswordDTORequest() {
	}

	public UserPasswordDTORequest(String username, String email, String name, Set<String> roles, String password) {
		super(username, email, name, roles);
		this.password = password;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return "UserPasswordDTORequest [" + super.toString() + "]";
	}

}
