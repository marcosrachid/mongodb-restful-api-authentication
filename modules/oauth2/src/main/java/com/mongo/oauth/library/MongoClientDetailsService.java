package com.mongo.oauth.library;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.ClientAlreadyExistsException;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.ClientRegistrationService;
import org.springframework.security.oauth2.provider.NoSuchClientException;

import com.mongo.domain.ClientDetails;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;

public class MongoClientDetailsService implements ClientDetailsService, ClientRegistrationService {

	private MongoTemplate mongoTemplate;

	private PasswordEncoder passwordEncoder;

	public MongoClientDetailsService(MongoTemplate mongoTemplate, PasswordEncoder passwordEncoder) {
		this.mongoTemplate = mongoTemplate;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public org.springframework.security.oauth2.provider.ClientDetails loadClientByClientId(String clientId)
			throws ClientRegistrationException {
		Query query = new Query();
		query.addCriteria(Criteria.where(ClientDetails.CLIENT_ID).is(clientId));
		ClientDetails clientDetails = mongoTemplate.findOne(query, ClientDetails.class);
		if (clientDetails == null) {
			throw new ClientRegistrationException(String.format("Client with id %s not found", clientId));
		}
		return clientDetails;
	}

	@Override
	public void addClientDetails(org.springframework.security.oauth2.provider.ClientDetails clientDetails)
			throws ClientAlreadyExistsException {
		if (loadClientByClientId(clientDetails.getClientId()) == null) {
			ClientDetails ClientDetails = new ClientDetails(clientDetails.getClientId(), clientDetails.getResourceIds(),
					clientDetails.isSecretRequired(), passwordEncoder.encode(clientDetails.getClientSecret()),
					clientDetails.isScoped(), clientDetails.getScope(), clientDetails.getAuthorizedGrantTypes(),
					clientDetails.getRegisteredRedirectUri(), clientDetails.getAuthorities(),
					clientDetails.getAccessTokenValiditySeconds(), clientDetails.getRefreshTokenValiditySeconds(),
					clientDetails.isAutoApprove("true"), clientDetails.getAdditionalInformation());
			mongoTemplate.save(ClientDetails);
		} else {
			throw new ClientAlreadyExistsException(
					String.format("Client with id %s already existed", clientDetails.getClientId()));
		}
	}

	@Override
	public void updateClientDetails(org.springframework.security.oauth2.provider.ClientDetails clientDetails)
			throws NoSuchClientException {
		Query query = new Query();
		query.addCriteria(Criteria.where(ClientDetails.CLIENT_ID).is(clientDetails.getClientId()));

		Update update = new Update();
		update.set(ClientDetails.RESOURCE_IDS, clientDetails.getResourceIds());
		update.set(ClientDetails.SCOPE, clientDetails.getScope());
		update.set(ClientDetails.AUTHORIZED_GRANT_TYPES, clientDetails.getAuthorizedGrantTypes());
		update.set(ClientDetails.REGISTERED_REDIRECT_URI, clientDetails.getRegisteredRedirectUri());
		update.set(ClientDetails.AUTHORITIES, clientDetails.getAuthorities());
		update.set(ClientDetails.ACCESS_TOKEN_VALIDITY_SECONDS, clientDetails.getAccessTokenValiditySeconds());
		update.set(ClientDetails.REFRESH_TOKEN_VALIDITY_SECONDS, clientDetails.getRefreshTokenValiditySeconds());
		update.set(ClientDetails.ADDITIONAL_INFORMATION, clientDetails.getAdditionalInformation());

		UpdateResult updateResult = mongoTemplate.updateFirst(query, update, ClientDetails.class);

		if (updateResult.getModifiedCount() <= 0) {
			throw new NoSuchClientException(String.format("Client with id %s not found", clientDetails.getClientId()));
		}
	}

	@Override
	public void updateClientSecret(String clientId, String clientSecret) throws NoSuchClientException {
		Query query = new Query();
		query.addCriteria(Criteria.where(ClientDetails.CLIENT_ID).is(clientId));

		Update update = new Update();
		update.set(ClientDetails.CLIENT_SECRET, passwordEncoder.encode(clientSecret));

		UpdateResult updateResult = mongoTemplate.updateFirst(query, update, ClientDetails.class);

		if (updateResult.getModifiedCount() <= 0) {
			throw new NoSuchClientException(String.format("Client with id %s not found", clientId));
		}
	}

	@Override
	public void removeClientDetails(String clientId) throws NoSuchClientException {
		Query query = new Query();
		query.addCriteria(Criteria.where(ClientDetails.CLIENT_ID).is(clientId));

		DeleteResult deleteResult = mongoTemplate.remove(query, ClientDetails.class);

		if (deleteResult.getDeletedCount() <= 0) {
			throw new NoSuchClientException(String.format("Client with id %s not found", clientId));
		}
	}

	@Override
	public List<org.springframework.security.oauth2.provider.ClientDetails> listClientDetails() {
		List<org.springframework.security.oauth2.provider.ClientDetails> result = new ArrayList<org.springframework.security.oauth2.provider.ClientDetails>();
		List<ClientDetails> details = mongoTemplate.findAll(ClientDetails.class);
		for (ClientDetails detail : details) {
			result.add(detail);
		}
		return result;
	}
}