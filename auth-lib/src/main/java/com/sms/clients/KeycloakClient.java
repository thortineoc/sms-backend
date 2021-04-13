package com.sms.clients;

import com.sms.authlib.TokenDTO;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Scope("prototype")
public class KeycloakClient {

    private static final String HAPROXY_URL = "http://52.142.201.18:24020";
    private static final String SMS_REALM = "sms";
    private static final String KEYCLOAK_ADMIN_URL = HAPROXY_URL + "/auth/admin/realms/" + SMS_REALM;
    private static final String ADMIN_CLIENT = "admin-cli";
    private static final String ADMIN_ACCOUNT_NAME = "kcuser";
    private static final String ADMIN_ACCOUNT_PASS = "kcuser";
    private static final String TOKEN_URL = HAPROXY_URL + "/auth/realms/master/protocol/openid-connect/token";

    private static final ExpirationTimer EXPIRATION_TIMER = ExpirationTimer.createStopped();
    private static TokenDTO adminToken;

    private final Configuration clientConfig = new ClientConfig().property(ClientProperties.SUPPRESS_HTTP_COMPLIANCE_VALIDATION, true);
    private final Client client = ClientBuilder.newClient(clientConfig);

    public InternalKeycloakClient request() {
        return new InternalKeycloakClient();
    }

    public class InternalKeycloakClient {

        private InternalKeycloakClient() {
            checkToken();
        }

        public boolean removeRoles(String userId, List<String> roles) {
            String url = String.format(KEYCLOAK_ADMIN_URL + "/users/%s/role-mappings/realm", userId);

            List<RoleRepresentation> roleRepresentations = roles.stream()
                    .map(role -> new RoleRepresentation(role, role, false))
                    .collect(Collectors.toList());

            // TODO: this is a hack, DELETE cannot have any content by http specifications
            Response response = client.target(url)
                    .request(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + adminToken.getAccessToken())
                    .method("DELETE", Entity.entity(roleRepresentations, MediaType.APPLICATION_JSON));

            return isResponseSuccessful(response);
        }

        public List<String> getRoles(String userId) {
            String url = String.format(KEYCLOAK_ADMIN_URL + "/users/%s/role-mappings/realm", userId);

            List<RoleRepresentation> roleRepresentations = Arrays.asList(client.target(url)
                    .request(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + adminToken.getAccessToken())
                    .get(RoleRepresentation[].class));

            return roleRepresentations.stream()
                    .map(RoleRepresentation::getName)
                    .collect(Collectors.toList());
        }

        public boolean addRoles(String userId, List<String> roles) {
            String url = String.format(KEYCLOAK_ADMIN_URL + "/users/%s/role-mappings/realm", userId);

            List<RoleRepresentation> roleRepresentations = roles.stream()
                    .map(role -> new RoleRepresentation(role, role, false))
                    .collect(Collectors.toList());

            Response response = client.target(url)
                    .request(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + adminToken.getAccessToken())
                    .post(Entity.entity(roleRepresentations, MediaType.APPLICATION_JSON));
            return isResponseSuccessful(response);
        }
    }

    private void checkToken() {
        if (adminToken == null || EXPIRATION_TIMER.isRefreshExpired()) {
            adminToken = obtainToken();
        } else if (EXPIRATION_TIMER.isExpired()) {
            adminToken = refreshToken(adminToken.getRefreshToken());
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

    private boolean isResponseSuccessful(Response response) {
        return Response.Status.Family.SUCCESSFUL.equals(response.getStatusInfo().getFamily());
    }
}
