package com.mongo.configuration;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.AccessTokenRequest;
import org.springframework.security.oauth2.client.token.DefaultAccessTokenRequest;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordResourceDetails;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;

@EnableOAuth2Client
@Configuration
public class OauthClientConfig {

	@Value("${oauth.token:http://localhost:8080/oauth/token}")
	private String tokenUrl;

	@Qualifier("User1")
	@Bean
	public OAuth2ProtectedResourceDetails resourceUser1() {
		ResourceOwnerPasswordResourceDetails resource = new ResourceOwnerPasswordResourceDetails();

		resource.setAccessTokenUri(tokenUrl);
		resource.setClientId("mongo");
		resource.setClientSecret("mongo");
		resource.setGrantType("password");
		resource.setScope(Stream.of("trusted").collect(Collectors.toList()));

		resource.setUsername("user1");
		resource.setPassword("pass1234");

		return resource;
	}

	@Qualifier("User2")
	@Bean
	public OAuth2ProtectedResourceDetails resourceUser2() {
		ResourceOwnerPasswordResourceDetails resource = new ResourceOwnerPasswordResourceDetails();

		resource.setAccessTokenUri(tokenUrl);
		resource.setClientId("mongo");
		resource.setClientSecret("mongo");
		resource.setGrantType("password");
		resource.setScope(Stream.of("trusted").collect(Collectors.toList()));

		resource.setUsername("user2");
		resource.setPassword("pass1234");

		return resource;
	}

	@Qualifier("restTemplateUser1")
	@Bean
	public OAuth2RestOperations restTemplateUser1() {
		AccessTokenRequest atr = new DefaultAccessTokenRequest();
		return new OAuth2RestTemplate(resourceUser1(), new DefaultOAuth2ClientContext(atr));
	}

	@Qualifier("restTemplateUser2")
	@Bean
	public OAuth2RestOperations restTemplateUser2() {
		AccessTokenRequest atr = new DefaultAccessTokenRequest();
		return new OAuth2RestTemplate(resourceUser2(), new DefaultOAuth2ClientContext(atr));
	}

}
