package com.mongo.oauth.library;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.mongo.domain.User;

public class MongoUserDetailsService implements UserDetailsService {

	private MongoTemplate mongoTemplate;

	public MongoUserDetailsService(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Query query = new Query();
		query.addCriteria(Criteria.where("username").is(username));
		User user = mongoTemplate.findOne(query, User.class);
		if (user == null) {
			throw new UsernameNotFoundException(String.format("Username %s not found", username));
		}

		String[] roles = new String[user.getRoles().size()];

		return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
				AuthorityUtils.createAuthorityList(user.getRoles().toArray(roles)));
	}
}