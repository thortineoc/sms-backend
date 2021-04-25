package com.sms.clients;

import com.sms.authlib.TokenDTO;
import com.sms.clients.entity.UserSearchParams;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

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

    private static final String HAPROXY_URL = "http://52.142.201.18:24020";
    private static final String SMS_REALM = "sms";
    private static final String KEYCLOAK_ADMIN_URL = HAPROXY_URL + "/auth/admin/realms/" + SMS_REALM;
    private static final String ADMIN_CLIENT = "admin-cli";
    private static final String ADMIN_ACCOUNT_NAME = "kcuser";
    private static final String ADMIN_ACCOUNT_PASS = "kcuser";
    private static final String BEARER = "Bearer ";
    private static final String TOKEN_URL = HAPROXY_URL + "/auth/realms/master/protocol/openid-connect/token";
    private static final String AUTHORIZATION = "Authorization";

    private static final ExpirationTimer EXPIRATION_TIMER = ExpirationTimer.createStopped();
    private static TokenDTO adminToken;

    private final Client client = ClientBuilder.newClient(
            new ClientConfig().property(ClientProperties.SUPPRESS_HTTP_COMPLIANCE_VALIDATION, true));

    // ################### USER API ###################

    public boolean createUser(UserRepresentation user) {
        checkToken();
        Response response = client.target(KEYCLOAK_ADMIN_URL + "/users")
                .request(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, BEARER + adminToken.getAccessToken())
                .post(Entity.entity(user, MediaType.APPLICATION_JSON));

        return isResponseSuccessful(response);
    }

    public boolean updateUser(String userId, UserRepresentation user) {
        checkToken();
        Response response = client.target(KEYCLOAK_ADMIN_URL + "/users/" + userId)
                .request(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, BEARER + adminToken.getAccessToken())
                .put(Entity.entity(user, MediaType.APPLICATION_JSON));

        return isResponseSuccessful(response);
    }

    public Optional<UserRepresentation> getUser(String userId) {
        checkToken();
        Response response = client.target(KEYCLOAK_ADMIN_URL + "/users/" + userId)
                .request(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, BEARER + adminToken.getAccessToken())
                .get();
        if (isResponseSuccessful(response)) {
            return Optional.of(response.readEntity(UserRepresentation.class));
        } else {
            return Optional.empty();
        }
    }

    public boolean deleteUser(String userId) {
        checkToken();
        Response response = client.target(KEYCLOAK_ADMIN_URL + "/users/" + userId)
                .request(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, BEARER + adminToken.getAccessToken())
                .delete();
        return isResponseSuccessful(response);
    }

    public List<UserRepresentation> getUsers(UserSearchParams searchParams) {
        checkToken();
        WebTarget target = searchParams.addParams(client.target(KEYCLOAK_ADMIN_URL + "/users"));
        Response response = target.request(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, BEARER + adminToken.getAccessToken())
                .get();
        if (isResponseSuccessful(response)) {
            return Arrays.asList(response.readEntity(UserRepresentation[].class));
        } else {
            return Collections.emptyList();
        }
    }

    // TODO: public only for integration tests, fix this
    public TokenDTO obtainToken() {
        try {
            MultivaluedMap<String, String> content = new MultivaluedHashMap<>();
            content.putSingle("username", ADMIN_ACCOUNT_NAME);
            content.putSingle("password", ADMIN_ACCOUNT_PASS);
            content.putSingle("client_id", ADMIN_CLIENT);
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
            content.putSingle("client_id", ADMIN_CLIENT);
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
