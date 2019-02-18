package com.mongo.domain.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.mongo.domain.User;

public interface UserRepository  extends MongoRepository<User, String> {
	
	Page<User> findAll(Pageable pageable);
	
	Optional<User> findOneByUsername(String login);
	
}