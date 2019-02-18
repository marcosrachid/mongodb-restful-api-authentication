package com.mongo.integration.steps;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.util.UriComponentsBuilder;

import com.google.common.collect.Sets;
import com.mongo.domain.constants.Role;
import com.mongo.domain.dto.ResponseDTO;
import com.mongo.domain.dto.request.UserPasswordDTORequest;
import com.mongo.integration.MongoIntegrationTest;

import cucumber.api.java.After;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

/**
 * 
 * @author Marcos Rachid
 *
 */
public class IdempotencySteps extends MongoIntegrationTest {

	private static final Logger LOG = LoggerFactory.getLogger(IdempotencySteps.class);

	private Exception e;
	private Exception eUser1;
	private Exception eUser2;

	private ResponseDTO user1Response;
	private ResponseDTO user2Response;

	public static final String BASE_URL = "http://localhost:8080/api/v1/";
	public static final String USER_URL = BASE_URL + "/users";
	public static final String USER_LOGIN_URL = USER_URL + "/{login}";
	public static final String USER_RESERVATIONS_URL = USER_LOGIN_URL + "/reservations";
	public static final String RESERVATIONS_URL = BASE_URL + "/reservations";
	public static final String RESERVATIONS_LOGIN_URL = RESERVATIONS_URL + "/{login}";

	@After
	public void end() {
		mongoTemplate.dropCollection("user");
		mongoTemplate.dropCollection("reservation");
		mongoTemplate.dropCollection("access_token");
		mongoTemplate.dropCollection("client_details");
		mongoTemplate.dropCollection("refresh_token");
	}

	@Given("^User creation (.+)$")
	public void createUser(String user) throws Throwable {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
			UserPasswordDTORequest json = new UserPasswordDTORequest();
			json.setUsername(user);
			json.setName("user");
			json.setEmail(user + "@" + user + ".com");
			json.setPassword("pass1234");
			json.setRoles(Sets.newHashSet(Role.USER));
			HttpEntity<String> request = new HttpEntity<>(objectMapper.writeValueAsString(json), headers);
			restTemplate.postForObject(USER_URL, request, ResponseDTO.class);
		} catch (Exception e) {
			this.e = e;
		}
	}

	@When("Both users try to request same reservation")
	public void parallelRequests() {
		try {
			final HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
			final URI uri = UriComponentsBuilder.fromUriString(RESERVATIONS_URL)
					.queryParam("startDate", LocalDate.now().plusDays(3).format(DateTimeFormatter.ISO_DATE))
					.queryParam("endDate", LocalDate.now().plusDays(4).format(DateTimeFormatter.ISO_DATE)).build()
					.toUri();
			Runnable run1 = () -> {
				LOG.debug("Starting thread from user1");
				try {
					this.user1Response = oAuth2RestTemplateUser1
							.exchange(uri, HttpMethod.POST, new HttpEntity<>(headers), ResponseDTO.class).getBody();
				} catch (Exception e) {
					this.eUser1 = e;
				}
				LOG.debug("Ending thread from user1");
			};
			Runnable run2 = () -> {
				LOG.debug("Starting thread from user2");
				try {
					this.user2Response = oAuth2RestTemplateUser2
							.exchange(uri, HttpMethod.POST, new HttpEntity<>(headers), ResponseDTO.class).getBody();
				} catch (Exception e) {
					this.eUser2 = e;
				}
				LOG.debug("Ending thread from user2");
			};
			ExecutorService executor = Executors.newFixedThreadPool(2);
			executor.submit(run1);
			executor.submit(run2);
			executor.awaitTermination(5, TimeUnit.SECONDS);
		} catch (Exception e) {
			this.e = e;
		}
	}

	@Then("Only one gets it throught")
	public void checkResults() {
		LOG.debug((user1Response != null) ? "user1 data: " + user1Response.getData()
				: "user1 exception: " + eUser1.getMessage());
		LOG.debug((user2Response != null) ? "user2 data: " + user2Response.getData()
				: "user2 exception: " + eUser2.getMessage());
		List user1List = (user1Response != null) ? (List) user1Response.getData() : new ArrayList<>();
		List user2List = (user2Response != null) ? (List) user2Response.getData() : new ArrayList<>();
		assertTrue((this.eUser2 != null && !user1List.isEmpty()) || (this.eUser1 != null && !user2List.isEmpty()));
		assertNull(e);
	}

}
