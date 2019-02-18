package com.mongo.oauth.utils;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongo.domain.dto.ErrorDTO;
import com.mongo.domain.dto.ResponseDTO;

public class CustomAccessDeniedHandler implements AccessDeniedHandler {

	private ObjectMapper objectMapper;

	public CustomAccessDeniedHandler(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException ad)
			throws IOException, ServletException {
		response.setContentType("application/json");
		response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		ResponseDTO dto = ResponseDTO.withError(new ErrorDTO(String.valueOf(HttpServletResponse.SC_FORBIDDEN),
				"Access denied, you don't have the necessary role to perform this"));
		response.getOutputStream().println(objectMapper.writeValueAsString(dto));
	}

}
