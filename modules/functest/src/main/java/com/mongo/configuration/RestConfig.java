package com.mongo.configuration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestConfig {

	@Qualifier("restTemplate")
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
	
}
