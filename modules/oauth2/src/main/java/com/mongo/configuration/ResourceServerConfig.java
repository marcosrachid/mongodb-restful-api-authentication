package com.mongo.configuration;

import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.CorsFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongo.domain.constants.Role;
import com.mongo.oauth.utils.CustomAccessDeniedHandler;
import com.mongo.oauth.utils.CustomAuthenticationEntryPoint;

/**
 * 
 * @author Marcos Rachid
 *
 */
@Configuration
@Order(1)
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

	private ObjectMapper objectMapper;

	private CorsFilter corsFilter;

	public ResourceServerConfig(ObjectMapper objectMapper, CorsFilter corsFilter) {
		this.objectMapper = objectMapper;
		this.corsFilter = corsFilter;
	}

	@Override
	public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
		resources.resourceId("mongo");
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {
		http.addFilter(corsFilter).exceptionHandling()
				.authenticationEntryPoint(new CustomAuthenticationEntryPoint(objectMapper))
				.accessDeniedHandler(new CustomAccessDeniedHandler(objectMapper)).and().csrf().disable().headers()
				.frameOptions().disable().and().sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().anonymous()
				.authorities(new String[] { Role.ANONYMOUS }).and().authorizeRequests()
				.antMatchers(HttpMethod.POST, "/api/v1/users").permitAll()
				.antMatchers(HttpMethod.GET, "/api/v1/reservations").permitAll()
				.antMatchers(HttpMethod.GET, "/api/v1/users").hasAuthority(Role.ADMIN)
				.antMatchers("/api/v1/users", "/api/v1/users/**", "/api/v1/reservations", "/api/v1/reservations/**")
				.hasAnyAuthority(Role.ADMIN, Role.USER);
	}

	@Bean
	public RestTemplate restTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
			@Override
			public void handleError(ClientHttpResponse response) throws IOException {
				if (response.getRawStatusCode() != 400) {
					super.handleError(response);
				}
			}
		});
		return restTemplate;
	}

	@Bean
	public RemoteTokenServices remoteTokenServices() {
		final RemoteTokenServices remoteTokenServices = new RemoteTokenServices();
		remoteTokenServices.setCheckTokenEndpointUrl("http://localhost:8080/oauth/check_token");
		remoteTokenServices.setClientId("mongo");
		remoteTokenServices.setClientSecret("mongo");
		remoteTokenServices.setRestTemplate(restTemplate());
		return remoteTokenServices;
	}

}
