package com.sms.tests.authlib;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.sms.authlib.TokenDTO;
import com.sms.clients.KeycloakClient;
import com.sms.clients.entity.UserSearchParams;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class KeycloakClientTest {

    private final static KeycloakClient CLIENT = new KeycloakClient();
    private final static String TEST_USER_ID = "a43856df-96bf-4747-b947-0b2b127ae677";
    private final static String PREFIX = "INTEGRATION_TESTS_";
    private final static String KOPYTKO54_USER = PREFIX + "kopytko54";
    private final static String TEST_ROLE = PREFIX + "role";

    @AfterAll
    static void cleanup() {
        CLIENT.request().deleteRole(TEST_ROLE);

        UserSearchParams params = new UserSearchParams().username(KOPYTKO54_USER);
        Optional<UserRepresentation> testUser = CLIENT.request().getUsers(params).stream().findFirst();
        testUser.ifPresent(userRepresentation -> CLIENT.request().deleteUser(userRepresentation.getId()));
    }

    @Test
    void shouldObtainAdminToken() {
        // WHEN
        TokenDTO token = CLIENT.obtainToken();

        // THEN
        assertIsToken(token);
    }

    @Test
    void shouldObtainAdminTokenWithRefreshToken() {
        // GIVEN
        TokenDTO adminToken = CLIENT.obtainToken();

        // WHEN
        TokenDTO refreshToken = CLIENT.refreshToken(adminToken.getRefreshToken());

        // THEN
        assertIsToken(refreshToken);
    }

    @Test
    void shouldGetRoleMappingsForTestUser() {
        // WHEN
        List<String> roles = CLIENT.request().getUserRoles(TEST_USER_ID);

        // THEN
        assertTrue(roles.contains("STUDENT"));
        assertTrue(roles.contains("USER"));
    }

    @Test
    void shouldGetKeycloakUserById() {
        // WHEN
        Optional<UserRepresentation> user = CLIENT.request().getUser(TEST_USER_ID);

        // THEN
        assertTrue(user.isPresent());
        assertEquals("testbackenduser", user.get().getUsername());
        assertEquals(TEST_USER_ID, user.get().getId());
    }

    @Test
    void shouldGetKeycloakUserByUsername() {
        // GIVEN
        UserSearchParams params = new UserSearchParams()
                .username("testbackenduser");

        // WHEN
        List<UserRepresentation> users = CLIENT.request().getUsers(params);

        // THEN
        assertEquals(1, users.size());

        UserRepresentation user = users.get(0);
        assertEquals("testbackenduser", user.getUsername());
        assertEquals(TEST_USER_ID, user.getId());
    }

    @Test
    void shouldGetAvailableRoles() {
        // WHEN
        List<RoleRepresentation> roles = CLIENT.request().getRoles();
        List<String> roleNames = roles.stream()
                .map(RoleRepresentation::getName)
                .collect(Collectors.toList());

        // THEN
        assertFalse(roleNames.isEmpty());
        assertTrue(roleNames.contains("STUDENT"));
        assertTrue(roleNames.contains("USER"));
        // TODO: add all of the roles here once they're added in keycloak
    }

    @Test
    void shouldCreateRoles() {
        // GIVEN
        String role = TEST_ROLE;

        // CREATE A ROLE
        boolean result = CLIENT.request().createRole(role);

        // THE ROLE SHOULD BE IN KEYCLOAK
        assertTrue(result);
        List<String> allRoles = CLIENT.request().getRoles().stream()
                .map(RoleRepresentation::getName)
                .collect(Collectors.toList());
        assertFalse(allRoles.isEmpty());
        assertTrue(allRoles.contains(role));

        // DELETE THE ROLE
        result = CLIENT.request().deleteRole(role);
        assertTrue(result);

        // THE ROLE SHOULD BE GONE FROM KEYCLOAK
        allRoles = CLIENT.request().getRoles().stream()
                .map(RoleRepresentation::getName)
                .collect(Collectors.toList());
        assertFalse(allRoles.isEmpty());
        assertFalse(allRoles.contains(role));
    }

    @Test
    void shouldCreateAndAssignRoleToUser() {
        // GIVEN
        String role = TEST_ROLE;

        // CREATE A ROLE
        boolean result = CLIENT.request().createRole(role);

        // THE ROLE SHOULD BE IN KEYCLOAK
        assertTrue(result);
        Optional<RoleRepresentation> savedRole = CLIENT.request().getRole(role);
        assertTrue(savedRole.isPresent());
        assertEquals(role, savedRole.get().getName());

        // ASSIGN THE ROLE TO "testbackenduser"
        result = CLIENT.request().assignRoles(TEST_USER_ID, Collections.singletonList(savedRole.get()));

        // THE USER SHOULD HAVE THE ROLE
        assertTrue(result);
        List<String> roles = CLIENT.request().getUserRoles(TEST_USER_ID);
        assertTrue(roles.contains(role));

        // UNASSIGN THE ROLE
        result = CLIENT.request().unassignRoles(TEST_USER_ID, Collections.singletonList(savedRole.get()));

        // THE USER SHOULD NO LONGER HAVE THAT ROLE
        assertTrue(result);
        roles = CLIENT.request().getUserRoles(TEST_USER_ID);
        assertFalse(roles.contains(role));

        // DELETE THE ROLE
        result = CLIENT.request().deleteRole(role);
        assertTrue(result);
    }

    @Test
    void shouldCreateUserInKeycloak() {
        // GIVEN
        UserRepresentation user = createUserRep(KOPYTKO54_USER, "kopytko54", "Tomasz", "Wojna",
                Lists.newArrayList("STUDENT", "USER"), "twojna@interia.pl", "3a");
        UserRepresentation updatedUser = createUserRep(KOPYTKO54_USER, "kopytko54", "Michał", "Stadryniak",
                Lists.newArrayList("STUDENT"), "twojna@interia.pl", "2g");

        // CREATE THE USER
        boolean result = CLIENT.request().createUser(user);

        // USER SHOULD BE CREATED IN KEYCLOAK
        assertTrue(result);
        UserSearchParams params = new UserSearchParams().username(KOPYTKO54_USER);
        UserRepresentation createdUser = CLIENT.request().getUsers(params).get(0);
        assertUsersAreEqual(user, createdUser);

        // THE USER SHOULD HAVE THE ROLES
        List<String> roles = CLIENT.request().getUserRoles(createdUser.getId());
        assertTrue(roles.contains("STUDENT"));
        assertTrue(roles.contains("USER"));

        // UPDATE USER DETAILS
        result = CLIENT.request().updateUser(createdUser.getId(), updatedUser);

        // THE USER DETAILS SHOULD BE UPDATED
        assertTrue(result);
        UserRepresentation updatedSavedUser = CLIENT.request().getUser(createdUser.getId()).get();
        assertUsersAreEqual(updatedUser, updatedSavedUser);

        // THE USER ROLES SHOULD BE UPDATED
        roles = CLIENT.request().getUserRoles(createdUser.getId());
        assertTrue(roles.contains("STUDENT"));
        assertFalse(roles.contains("USER"));

        // DELETE THE USER
        result = CLIENT.request().deleteUser(createdUser.getId());
        assertTrue(result);

        // THE USER SHOULD BE DELETED
        Optional<UserRepresentation> deletedUser = CLIENT.request().getUser(createdUser.getId());
        assertFalse(deletedUser.isPresent());
    }

    private UserRepresentation createUserRep(String username, String password, String firstName, String lastName,
                                             List<String> roles, String email, String group) {
        UserRepresentation userRep = new UserRepresentation();
        userRep.setEmail(email);
        userRep.setUsername(username);
        userRep.setFirstName(firstName);
        userRep.setLastName(lastName);
        userRep.setRealmRoles(roles);
        userRep.setCredentials(Collections.singletonList(getPasswordCredential(password)));

        // TODO: make sure this works and users returned from keycloak have this attribute mapped
        userRep.setAttributes(ImmutableMap.of("group", Collections.singletonList(group)));
        return userRep;
    }

    private CredentialRepresentation getPasswordCredential(String password) {
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType("password");
        credential.setValue(password);
        credential.setTemporary(false);
        return credential;
    }

    private void assertUsersAreEqual(UserRepresentation expected, UserRepresentation real) {
        assertEquals(expected.getUsername().toLowerCase(Locale.ROOT), real.getUsername().toLowerCase(Locale.ROOT));
        assertEquals(expected.getEmail(), real.getEmail());
        assertEquals(expected.getFirstName(), real.getFirstName());
        assertEquals(expected.getLastName(), real.getLastName());
        assertEquals(expected.getAttributes(), real.getAttributes());

        // NOTE: users returned from keycloak don't have their roles, this is a bug in keycloak
//        assertEquals(expected.getRealmRoles(), real.getRealmRoles());
    }

    private void assertIsToken(TokenDTO token) {
        assertNotNull(token.getAccessToken());
        assertNotNull(token.getExpiration());
        assertNotNull(token.getRefreshExpiration());
        assertNotNull(token.getRefreshToken());
        assertNotNull(token.getTokenType());
        assertNotNull(token.getNotBeforePolicy());
        assertNotNull(token.getSessionState());
        assertNotNull(token.getScope());
    }
}
