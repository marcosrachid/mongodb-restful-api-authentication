package com.mongo.domain;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.google.common.collect.Sets;
import com.mongo.domain.constants.Role;

@Document(collection = "user")
public class User implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	private String id;
	
	@Indexed(unique=true)
	private String username;

	@Indexed(unique=true)
	private String email;

	private String name;

	private String password;

	private Set<String> roles;
	
	private boolean active = true;

	private LocalDateTime createDate;
	
	private LocalDateTime modifiedDate;

	public User() {
		this.roles = Sets.newHashSet(Role.USER);
		this.createDate = LocalDateTime.now();
		this.modifiedDate = LocalDateTime.now();
	}

	public User(String username, String email, String name, String password) {
		super();
		this.username = username;
		this.email = email;
		this.name = name;
		this.password = password;
		this.roles = Sets.newHashSet(Role.USER);
		this.createDate = LocalDateTime.now();
		this.modifiedDate = LocalDateTime.now();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Set<String> getRoles() {
		return roles;
	}

	public void setRoles(Set<String> roles) {
		this.roles = roles;
	}

	public User addRole(String role) {
		this.roles.add(role);
		return this;
	}

	public User removeRole(String role) {
		this.roles.remove(role);
		return this;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public LocalDateTime getCreateDate() {
		return createDate;
	}

	public void setCreateDate(LocalDateTime createDate) {
		this.createDate = createDate;
	}

	public LocalDateTime getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(LocalDateTime modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
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
		User other = (User) obj;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", username=" + username + ", email=" + email + ", name=" + name + ", roles=" + roles
				+ ", active=" + active + ", createDate=" + createDate + ", modifiedDate=" + modifiedDate + "]";
	}

}
