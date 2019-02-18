package com.mongo.oauth.utils;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongo.domain.dto.ErrorDTO;
import com.mongo.domain.dto.ResponseDTO;

public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

	private ObjectMapper objectMapper;

	public CustomAuthenticationEntryPoint(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	/**
	 * Setting Http Response 401 and system message
	 * 
	 */
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException ae)
			throws IOException, ServletException {
		response.setContentType("application/json");
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		ResponseDTO dto = ResponseDTO.withError(new ErrorDTO(String.valueOf(HttpServletResponse.SC_UNAUTHORIZED),
				"Unauthorized access", null, "It's necessary an authentication token"));
		response.getOutputStream().println(objectMapper.writeValueAsString(dto));
	}

}