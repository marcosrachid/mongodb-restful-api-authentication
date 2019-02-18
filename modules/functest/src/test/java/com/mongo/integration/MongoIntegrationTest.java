package com.mongo.integration;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongo.MongoApplication;

/**
 * 
 * @author Marcos Rachid
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ContextConfiguration(classes = { MongoApplication.class })
public abstract class MongoIntegrationTest {

	@Qualifier("restTemplateUser1")
	@Autowired
	protected OAuth2RestTemplate oAuth2RestTemplateUser1;

	@Qualifier("restTemplateUser2")
	@Autowired
	protected OAuth2RestTemplate oAuth2RestTemplateUser2;
	
	@Qualifier("restTemplate")
	@Autowired
	protected RestTemplate restTemplate;
	
	@Autowired
	protected MongoTemplate mongoTemplate;
	
	@Autowired
	protected ObjectMapper objectMapper;

}
