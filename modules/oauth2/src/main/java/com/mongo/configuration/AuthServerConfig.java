package com.mongo.configuration;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;

import com.mongo.oauth.library.MongoClientDetailsService;
import com.mongo.oauth.library.MongoTokenStore;

@Configuration
@EnableAuthorizationServer
public class AuthServerConfig extends AuthorizationServerConfigurerAdapter {

	private MongoTemplate mongoTemplate;

	private PasswordEncoder passwordEncoder;

	private AuthenticationManager authenticationManager;

	public AuthServerConfig(MongoTemplate mongoTemplate, PasswordEncoder passwordEncoder,
			@Qualifier("authenticationManagerBean") AuthenticationManager authenticationManager) {
		this.mongoTemplate = mongoTemplate;
		this.passwordEncoder = passwordEncoder;
		this.authenticationManager = authenticationManager;
	}

	@Bean
	public MongoClientDetailsService clientDetailsService() {
		return new MongoClientDetailsService(mongoTemplate, passwordEncoder);
	}

	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		clients.withClientDetails(clientDetailsService());
	}

	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		endpoints.tokenServices(tokenServices()).authenticationManager(authenticationManager);
	}

	@Override
	public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
		oauthServer.tokenKeyAccess("permitAll()").checkTokenAccess("isAuthenticated()");
		oauthServer.allowFormAuthenticationForClients();
	}

	@Primary
	@Bean
	public DefaultTokenServices tokenServices() {
		DefaultTokenServices tokenServices = new DefaultTokenServices();
		tokenServices.setSupportRefreshToken(true);
		tokenServices.setTokenStore(tokenStore());

		List<TokenEnhancer> enhancers = new ArrayList<>();

		enhancers.add(new TokenEnhancer() {

			public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
				final Authentication userAuthentication = authentication.getUserAuthentication();

				final DefaultOAuth2AccessToken defaultOAuth2AccessToken = (DefaultOAuth2AccessToken) accessToken;
				Set<String> existingScopes = new HashSet<>();
				existingScopes.addAll(defaultOAuth2AccessToken.getScope());
				if (userAuthentication != null) {
					existingScopes.add("trusted");
				} else { 
					
					existingScopes.add("non-trusted");
				}

				defaultOAuth2AccessToken.setScope(existingScopes);
				return defaultOAuth2AccessToken;
			}
		});

		TokenEnhancerChain enhancerChain = new TokenEnhancerChain();
		enhancerChain.setTokenEnhancers(enhancers);
		tokenServices.setTokenEnhancer(enhancerChain);

		return tokenServices;
	}

	@Bean
	public TokenStore tokenStore() {
		return new MongoTokenStore(mongoTemplate);
	}
}