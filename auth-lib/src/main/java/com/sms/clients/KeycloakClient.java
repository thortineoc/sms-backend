package com.sms.clients;

import com.sms.api.authlib.TokenDTO;
import com.sms.clients.entity.KcResult;
import com.sms.clients.entity.UserSearchParams;
import com.sms.context.SmsConfiguration;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.*;
import java.util.*;

@Component
@Scope("prototype")
public class KeycloakClient {

    private String KEYCLOAK_ADMIN_URL;
    private String TOKEN_URL;

    private static final String BEARER = "Bearer ";
    private static final String AUTHORIZATION = "Authorization";

    private static final ExpirationTimer EXPIRATION_TIMER = ExpirationTimer.createStopped();
    private static TokenDTO adminToken;

    private final Client client = ClientBuilder.newClient(
            new ClientConfig().property(ClientProperties.SUPPRESS_HTTP_COMPLIANCE_VALIDATION, true));

    public KeycloakClient() {
    }

    public KeycloakClient(String haproxyUrl, String realmName) {
        KEYCLOAK_ADMIN_URL = haproxyUrl + "/auth/admin/realms/" + realmName;
        TOKEN_URL = haproxyUrl + "/auth/realms/master/protocol/openid-connect/token";
    }

    @PostConstruct
    private void init() {
        KEYCLOAK_ADMIN_URL = SmsConfiguration.haproxyUrl + "/auth/admin/realms/" + SmsConfiguration.realmName;
        TOKEN_URL = SmsConfiguration.haproxyUrl + "/auth/realms/master/protocol/openid-connect/token";
    }

    // ################### USER API ###################

    public KcResult<Object> createUser(UserRepresentation user) {
        checkToken();
        user.setEnabled(true);
        Response response = client.target(KEYCLOAK_ADMIN_URL + "/users")
                .request(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, BEARER + adminToken.getAccessToken())
                .post(Entity.entity(user, MediaType.APPLICATION_JSON));

        if (isResponseSuccessful(response)) {
            return KcResult.ok(null);
        } else {
            return KcResult.fail(response.getStatus());
        }
    }

    public KcResult<Object> updateUser(String userId, UserRepresentation user) {
        checkToken();
        Response response = client.target(KEYCLOAK_ADMIN_URL + "/users/" + userId)
                .request(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, BEARER + adminToken.getAccessToken())
                .put(Entity.entity(user, MediaType.APPLICATION_JSON));

        if (isResponseSuccessful(response)) {
            return KcResult.ok(null);
        } else {
            return KcResult.fail(response.getStatus());
        }
    }

    public KcResult<UserRepresentation> getUser(String userId) {
        checkToken();
        Response response = client.target(KEYCLOAK_ADMIN_URL + "/users/" + userId)
                .request(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, BEARER + adminToken.getAccessToken())
                .get();
        // KC returns 404 on non existent user, we return 204
        if (isResponseSuccessful(response)) {
            return KcResult.ok(response.readEntity(UserRepresentation.class));
        } else if (response.getStatus() == 404) {
            return KcResult.ok(null);
        } else {
            return KcResult.fail(response.getStatus());
        }
    }

    public KcResult<Object> deleteUser(String userId) {
        checkToken();
        Response response = client.target(KEYCLOAK_ADMIN_URL + "/users/" + userId)
                .request(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, BEARER + adminToken.getAccessToken())
                .delete();

        if (isResponseSuccessful(response)) {
            return KcResult.ok(null);
        } {
            return KcResult.fail(response.getStatus());
        }
    }

    public KcResult<List<UserRepresentation>> getUsers(UserSearchParams searchParams) {
        checkToken();
        WebTarget target = searchParams.addParams(client.target(KEYCLOAK_ADMIN_URL + "/users"));
        Response response = target.request(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, BEARER + adminToken.getAccessToken())
                .get();
        if (isResponseSuccessful(response)) {
            List<UserRepresentation> users = Arrays.asList(response.readEntity(UserRepresentation[].class));
            return KcResult.ok(users);
        } else {
            return KcResult.fail(response.getStatus());
        }
    }

    // TODO: public only for integration tests, fix this
    public TokenDTO obtainToken() {
        try {
            MultivaluedMap<String, String> content = new MultivaluedHashMap<>();
            content.putSingle("username", SmsConfiguration.adminUsername);
            content.putSingle("password", SmsConfiguration.adminPassword);
            content.putSingle("client_id", SmsConfiguration.adminClient);
            content.putSingle("grant_type", "password");

            Response response = client.target(TOKEN_URL)
                    .request(MediaType.APPLICATION_FORM_URLENCODED)
                    .post(Entity.form(content));
            TokenDTO token = response.readEntity(TokenDTO.class);
            EXPIRATION_TIMER.reset();
            EXPIRATION_TIMER.start(token.getExpiration(), token.getRefreshExpiration());

            return token;
        } catch (Exception e) {
            throw new BadRequestException("Obtaining the token failed: " + e);
        }
    }

    // TODO: public only for integration tests, fix this
    public TokenDTO refreshToken(String refreshToken) {
        try {
            MultivaluedMap<String, String> content = new MultivaluedHashMap<>();
            content.putSingle("client_id", SmsConfiguration.adminClient);
            content.putSingle("grant_type", "refresh_token");
            content.putSingle("refresh_token", refreshToken);

            Response response = client.target(TOKEN_URL)
                    .request(MediaType.APPLICATION_FORM_URLENCODED)
                    .post(Entity.form(content));
            TokenDTO token = response.readEntity(TokenDTO.class);
            EXPIRATION_TIMER.reset();
            EXPIRATION_TIMER.start(token.getExpiration(), token.getRefreshExpiration());

            return token;
        } catch (Exception e) {
            throw new BadRequestException("Refreshing the token failed: " + e);
        }
    }

    private void checkToken() {
        if (adminToken == null || EXPIRATION_TIMER.isRefreshExpired()) {
            adminToken = obtainToken();
        } else if (EXPIRATION_TIMER.isExpired()) {
            adminToken = refreshToken(adminToken.getRefreshToken());
        }
    }

    private boolean isResponseSuccessful(Response response) {
        return Response.Status.Family.SUCCESSFUL.equals(response.getStatusInfo().getFamily());
    }
}
