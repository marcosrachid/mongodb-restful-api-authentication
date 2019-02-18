package com.mongo.controller.exception.handler;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.METHOD_NOT_ALLOWED;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.mongo.domain.dto.ErrorDTO;
import com.mongo.domain.dto.ResponseDTO;
import com.mongo.service.exception.BusinessException;

/**
 * 
 * @author Marcos Rachid
 *
 */
@ControllerAdvice(basePackages = "com.mongo.controller")
public class ControllerExceptionHandler {

	private static final Logger LOG = LoggerFactory.getLogger(ControllerExceptionHandler.class);

	private static final String WEB_ERROR = "[Web] An error has ocurred on web endpoint request. ";

	/**
	 * 
	 * @param e
	 * @return
	 */
	@ExceptionHandler({ BadCredentialsException.class, AuthenticationException.class, InvalidTokenException.class })
	public ResponseEntity<ResponseDTO> handleAuthenticationException(AuthenticationException e) {
		LOG.debug(WEB_ERROR, e);
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).contentType(APPLICATION_JSON_UTF8)
				.body(ResponseDTO.withError(new ErrorDTO(HttpStatus.UNAUTHORIZED.toString(), e.getMessage())));
	}

	/**
	 * 
	 * @param e
	 * @return
	 */
	@ExceptionHandler(ConcurrencyFailureException.class)
	public ResponseEntity<ResponseDTO> processConcurrencyError(ConcurrencyFailureException e) {
		LOG.error(WEB_ERROR, e);
		return ResponseEntity.status(CONFLICT)
				.body(ResponseDTO.withError(new ErrorDTO(CONFLICT.toString(), e.getMessage())));
	}

	/**
	 *
	 * @param e
	 * @return ResponseEntity<ResponseDTO>
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ResponseDTO> processValidationError(MethodArgumentNotValidException e) {
		LOG.debug(WEB_ERROR, e);

		BindingResult result = e.getBindingResult();
		List<ErrorDTO> errors = new ArrayList<>();
		result.getAllErrors().forEach(item -> {
			ErrorDTO errorDTO = new ErrorDTO(BAD_REQUEST.toString(),
					"Attribute " + item.getDefaultMessage().toLowerCase(), getAttibuteNameFromMessageError(item));
			errors.add(errorDTO);
		});

		return ResponseEntity.status(BAD_REQUEST).body(ResponseDTO.withErrors(errors));
	}

	/**
	 * 
	 * @param e
	 * @return
	 */
	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ResponseDTO> processAccessDeniedException(AccessDeniedException e) {
		LOG.debug(WEB_ERROR, e);
		return ResponseEntity.status(FORBIDDEN)
				.body(ResponseDTO.withError(new ErrorDTO(FORBIDDEN.toString(), e.getMessage())));
	}

	/**
	 * 
	 * @param e
	 * @return
	 */
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ResponseEntity<ResponseDTO> processMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
		LOG.debug(WEB_ERROR, e);
		return ResponseEntity.status(METHOD_NOT_ALLOWED)
				.body(ResponseDTO.withError(new ErrorDTO(METHOD_NOT_ALLOWED.toString(), e.getMessage())));
	}

	/**
	 * It generally means that an enumerated field was informed with an invalid
	 * value.
	 * 
	 * @param e The exception that triggered the Advice.
	 * @return The response with the proper error.
	 */
	@ExceptionHandler({ MethodArgumentTypeMismatchException.class, IllegalArgumentException.class })
	public ResponseEntity<ResponseDTO> argumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
		LOG.debug(WEB_ERROR, e);
		return ResponseEntity.status(BAD_REQUEST)
				.body(ResponseDTO.withError(new ErrorDTO(BAD_REQUEST.toString(), e.getMessage())));
	}

	/**
	 * 
	 * @param e
	 * @return
	 */
	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<ResponseDTO> handleBlockException(BusinessException e) {
		LOG.debug(WEB_ERROR, e);
		return ResponseEntity.status(BAD_REQUEST)
				.body(ResponseDTO.withError(new ErrorDTO(BAD_REQUEST.toString(), e.getMessage())));
	}

	/**
	 *
	 * @param e
	 * @return ResponseEntity<ResponseDTO>
	 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ResponseDTO> handleException(Exception e) {
		LOG.error(WEB_ERROR, e);
		return ResponseEntity.status(INTERNAL_SERVER_ERROR)
				.body(ResponseDTO.withError(new ErrorDTO(INTERNAL_SERVER_ERROR.toString(),
						"An internal server error occured at "
								+ LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME),
						null, "Notify your administrator")));
	}

	/**
	 *
	 * @param item
	 * @return String
	 */
	private static String getAttibuteNameFromMessageError(ObjectError item) {
		final Pattern p = Pattern.compile("\\[(.[a-zA-z]+)\\]");
		Matcher m = p.matcher(StringUtils.arrayToDelimitedString(item.getArguments(), ","));
		return m.find() ? m.group(0) : null;
	}

}