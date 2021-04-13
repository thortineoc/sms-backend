package com.sms.clients;

import com.sms.authlib.TokenDTO;
import com.sms.clients.entity.UserSearchParams;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.*;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
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

        // ################### USER API ###################

        public boolean createUser(UserRepresentation user) {
            Response response = client.target(KEYCLOAK_ADMIN_URL + "/users")
                    .request(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + adminToken.getAccessToken())
                    .post(Entity.entity(user, MediaType.APPLICATION_JSON));
            if (!isResponseSuccessful(response)) {
                return false;
            }

            //NOTE: due to a bug in keycloak we have to assign the roles separately, after creating the user
            if (!assignRolesByUsername(user)) {
                return false;
            }

            return isResponseSuccessful(response);
        }

        public boolean updateUser(String userId, UserRepresentation user) {
            Response response = client.target(KEYCLOAK_ADMIN_URL + "/users/" + userId)
                    .request(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + adminToken.getAccessToken())
                    .put(Entity.entity(user, MediaType.APPLICATION_JSON));

            //NOTE: due to a bug in keycloak we have to assign the roles separately, after creating the user
            if (!user.getRealmRoles().isEmpty()) {

                // TODO: this can be optimized, query only for the sum of the set of old and new roles
                List<RoleRepresentation> oldRoles = getRoleDetails(getUserRoles(userId));
                List<RoleRepresentation> newRoles = getRoleDetails(user.getRealmRoles());
                if (!unassignRoles(userId, oldRoles)) {
                    return false;
                }
                if (!assignRoles(userId, newRoles)) {
                    return false;
                }
            }
            return isResponseSuccessful(response);
        }

        private boolean assignRolesByUsername(UserRepresentation user) {
            if (user.getRealmRoles() == null || user.getRealmRoles().isEmpty()) {
                return true;
            }

            Optional<UserRepresentation> savedUser = getUsers(new UserSearchParams().username(user.getUsername()))
                    .stream().findFirst();
            if (!savedUser.isPresent()) {
                return false;
            }

            // TODO: this queries roles one by one, find out if there's API for getting multiple roles
            List<RoleRepresentation> roles = getRoleDetails(user.getRealmRoles());
            return assignRoles(savedUser.get().getId(), roles);
        }

        private List<RoleRepresentation> getRoleDetails(List<String> roles) {
            return roles.stream()
                    .map(this::getRole)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList());
        }

        public Optional<UserRepresentation> getUser(String userId) {
            Response response = client.target(KEYCLOAK_ADMIN_URL + "/users/" + userId)
                    .request(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + adminToken.getAccessToken())
                    .get();
            if (isResponseSuccessful(response)) {
                return Optional.of(response.readEntity(UserRepresentation.class));
            } else {
                return Optional.empty();
            }
        }

        public boolean deleteUser(String userId) {
            Response response = client.target(KEYCLOAK_ADMIN_URL + "/users/" + userId)
                    .request(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + adminToken.getAccessToken())
                    .delete();

            return isResponseSuccessful(response);
        }

        public List<UserRepresentation> getUsers(UserSearchParams searchParams) {
            WebTarget target = searchParams.addParams(client.target(KEYCLOAK_ADMIN_URL + "/users"));

            Response response = target.request(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + adminToken.getAccessToken())
                    .get();

            return Arrays.asList(response.readEntity(UserRepresentation[].class));
        }

        // ################### ROLES API ###################

        public boolean createRole(String name) {
            RoleRepresentation role = new RoleRepresentation(name, name, false);

            Response response = client.target(KEYCLOAK_ADMIN_URL + "/roles")
                    .request(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + adminToken.getAccessToken())
                    .post(Entity.entity(role, MediaType.APPLICATION_JSON));

            return isResponseSuccessful(response);
        }

        public boolean deleteRole(String name) {
            Response response = client.target(KEYCLOAK_ADMIN_URL + "/roles/" + name)
                    .request(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + adminToken.getAccessToken())
                    .delete();

            return isResponseSuccessful(response);
        }

        public Optional<RoleRepresentation> getRole(String name) {
            Response response = client.target(KEYCLOAK_ADMIN_URL + "/roles/" + name)
                    .request(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + adminToken.getAccessToken())
                    .get();
            if (isResponseSuccessful(response)) {
                return Optional.of(response.readEntity(RoleRepresentation.class));
            } else {
                return Optional.empty();
            }
        }

        public List<RoleRepresentation> getRoles() {
            Response response = client.target(KEYCLOAK_ADMIN_URL + "/roles")
                    .request(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + adminToken.getAccessToken())
                    .get();

            return Arrays.asList(response.readEntity(RoleRepresentation[].class));
        }

        public boolean unassignRoles(String userId, List<RoleRepresentation> roles) {
            String url = String.format(KEYCLOAK_ADMIN_URL + "/users/%s/role-mappings/realm", userId);

            // TODO: this is a hack, DELETE cannot have any content by http specifications
            Response response = client.target(url)
                    .request(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + adminToken.getAccessToken())
                    .method("DELETE", Entity.entity(roles, MediaType.APPLICATION_JSON));

            return isResponseSuccessful(response);
        }

        public List<String> getUserRoles(String userId) {
            String url = String.format(KEYCLOAK_ADMIN_URL + "/users/%s/role-mappings/realm", userId);

            List<RoleRepresentation> roleRepresentations = Arrays.asList(client.target(url)
                    .request(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + adminToken.getAccessToken())
                    .get(RoleRepresentation[].class));

            return roleRepresentations.stream()
                    .map(RoleRepresentation::getName)
                    .collect(Collectors.toList());
        }

        public boolean assignRoles(String userId, List<RoleRepresentation> roles) {
            String url = String.format(KEYCLOAK_ADMIN_URL + "/users/%s/role-mappings/realm", userId);

            Response response = client.target(url)
                    .request(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + adminToken.getAccessToken())
                    .post(Entity.entity(roles, MediaType.APPLICATION_JSON));
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
