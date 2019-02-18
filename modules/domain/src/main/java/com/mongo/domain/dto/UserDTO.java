package com.mongo.domain.dto;

import java.io.Serializable;
import java.util.Set;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.mongo.domain.User;

public class UserDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final int EMAIL_MIN_LENGTH = 3;

	public static final int NAME_MIN_LENGTH = 1;
	
	@NotNull
	protected String username;

	@NotNull
	@Size(min = EMAIL_MIN_LENGTH)
	protected String email;

	@NotNull
	@Size(min = NAME_MIN_LENGTH)
	protected String name;

	@NotNull
	protected Set<String> roles;

	public UserDTO() {
	}

	public UserDTO(User user) {
		this(user.getUsername(), user.getEmail(), user.getName(), user.getRoles());
	}

	public UserDTO(String username, String email, String name, Set<String> roles) {
		super();
		this.username = username;
		this.email = email;
		this.name = name;
		this.roles = roles;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<String> getRoles() {
		return roles;
	}

	public void setRoles(Set<String> roles) {
		this.roles = roles;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((roles == null) ? 0 : roles.hashCode());
		result = prime * result + ((username == null) ? 0 : username.hashCode());
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
		UserDTO other = (UserDTO) obj;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (roles == null) {
			if (other.roles != null)
				return false;
		} else if (!roles.equals(other.roles))
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "UserDTO [username=" + username + ", email=" + email + ", name=" + name + ", roles=" + roles + "]";
	}

}
