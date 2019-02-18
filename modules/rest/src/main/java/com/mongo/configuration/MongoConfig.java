package com.mongo.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.mongodb.MongoClient;

@Configuration
@EnableMongoRepositories(basePackages = "com.mongo.domain.repository")
public class MongoConfig extends AbstractMongoConfiguration {
	
	@Value("${spring.mongodb.host:127.0.0.1}")
	private String host;
	
	@Value("${spring.mongodb.port:27017}")
	private Integer port;
	
	@Value("${spring.mongodb.name:mongo}")
	private String name;

	@Override
	public MongoClient mongoClient() {
		return new MongoClient(host, port);
	}

	@Override
	protected String getDatabaseName() {
		return name;
	}

}
