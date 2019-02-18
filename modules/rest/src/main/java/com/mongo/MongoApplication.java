package com.mongo;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import com.google.common.collect.Sets;
import com.mongo.domain.ClientDetails;
import com.mongo.domain.Reservation;
import com.mongo.domain.User;
import com.mongo.domain.constants.ClientRole;
import com.mongo.domain.constants.Role;

/**
 * Application startup
 * 
 * @author Marcos Rachid
 *
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.up")
@PropertySources(value = { @PropertySource("classpath:mongo/application.properties") })
public class MongoApplication {

	private static final Logger LOG = LoggerFactory.getLogger(MongoApplication.class);

	@Value("${application.admin.password:admin}")
	private String adminPassword;

	@Value("${application.min.reservation:1}")
	private Long minDayFromCurrentAbleToReserve; // 1 day default

	@Value("${application.max.reservation:30}")
	private Long maxDayFromCurrentAbleToReserve; // 1 month default

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private PasswordEncoder passwordEncoder;

	/**
	 * Main method, used to run the application.
	 *
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		ApplicationContext ctx = SpringApplication.run(MongoApplication.class, args);

		LOG.debug("Let's inspect the beans provided by Spring Boot:");

		String[] beanNames = ctx.getBeanDefinitionNames();
		Arrays.sort(beanNames);
		for (String beanName : beanNames) {
			LOG.debug(beanName);
		}
	}

	@Bean
	public LocaleResolver localeResolver() {
		SessionLocaleResolver sessionLocaleResolver = new SessionLocaleResolver();
		Locale locale = new Locale("en", "US");
		sessionLocaleResolver.setDefaultLocale(locale);
		return sessionLocaleResolver;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	/**
	 * Every midnight a new Reservation day is opened
	 */
	@Scheduled(cron = "0 0 0 * * *", zone = "UTC")
	public synchronized void openNewReservationDate() {
		mongoTemplate.save(new Reservation(LocalDate.now().plusDays(maxDayFromCurrentAbleToReserve)), "reservation");
	}

	/**
	 * Admin creation
	 * 
	 * @param mongoTemplate
	 */
	@PostConstruct
	public void createAdmin() {
		// init admin user
		User user = mongoTemplate.findOne(Query.query(Criteria.where("username").is("admin")), User.class);
		if (user == null) {
			user = new User("admin", "admin@admin.com", "admin", passwordEncoder.encode(adminPassword));
			user.addRole(Role.ADMIN);
			mongoTemplate.save(user, "user");
		}

		// init the client details
		ClientDetails clientDetails = mongoTemplate.findOne(Query.query(Criteria.where("clientId").is("mongo")),
				ClientDetails.class);
		if (clientDetails == null) {
			clientDetails = new ClientDetails();
			clientDetails.setClientId("mongo");
			clientDetails.setClientSecret(passwordEncoder.encode("mongo"));
			clientDetails.setSecretRequired(true);
			clientDetails.setResourceIds(Sets.newHashSet("mongo"));
			clientDetails.setScope(Stream.of("trusted").collect(Collectors.toSet()));
			clientDetails.setAuthorizedGrantTypes(Sets.newHashSet("refresh_token", "password"));
			clientDetails.setRegisteredRedirectUri(Sets.newHashSet());
			clientDetails.setAuthorities(AuthorityUtils.createAuthorityList(ClientRole.listAsString()));
			clientDetails.setAccessTokenValiditySeconds(60);
			clientDetails.setRefreshTokenValiditySeconds(14400);
			clientDetails.setAutoApprove(false);
			mongoTemplate.save(clientDetails, "client_details");
		}

		LocalDate startingAbleDay = LocalDate.now().plusDays(minDayFromCurrentAbleToReserve);
		LocalDate lastingAbleDay = LocalDate.now().plusDays(maxDayFromCurrentAbleToReserve);
		List<Reservation> dates = Stream.iterate(startingAbleDay, d -> d.plusDays(1))
				.limit(ChronoUnit.DAYS.between(startingAbleDay, lastingAbleDay) + 1).map(d -> new Reservation(d))
				.collect(Collectors.toList());
		List<Reservation> reservations = mongoTemplate
				.find(Query.query(Criteria.where("reservationDate").gte(startingAbleDay).lte(lastingAbleDay)), Reservation.class);
		dates.removeAll(reservations);
		mongoTemplate.insertAll(dates);
	}

}
