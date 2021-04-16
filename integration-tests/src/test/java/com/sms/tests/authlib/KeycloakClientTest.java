package com.sms.tests.authlib;

import com.sms.authlib.TokenDTO;
import com.sms.clients.KeycloakClient;
import com.sms.clients.entity.UserSearchParams;
import org.checkerframework.checker.nullness.qual.AssertNonNullIfNonNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import javax.validation.constraints.AssertTrue;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class KeycloakClientTest {

    private final static KeycloakClient CLIENT = new KeycloakClient();
    private final static String TEST_USER_ID = "a43856df-96bf-4747-b947-0b2b127ae677";
    private final static String PREFIX = "INTEGRATION_TESTS_";
    private final static String KOPYTKO54_USER = PREFIX + "kopytko54";

    @AfterAll
    static void cleanup() {

        UserSearchParams params = new UserSearchParams().username(KOPYTKO54_USER);
        Optional<UserRepresentation> testUser = CLIENT.getUsers(params).stream().findFirst();
        testUser.ifPresent(userRepresentation -> CLIENT.deleteUser(userRepresentation.getId()));
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
    void shouldGetKeycloakUserById() {
        // WHEN
        Optional<UserRepresentation> user = CLIENT.getUser(TEST_USER_ID);

        // THEN
        assertTrue(user.isPresent());
        assertEquals("testbackenduser", user.get().getUsername());
        assertEquals(TEST_USER_ID, user.get().getId());
    }

    @Test
    void shouldGetKeycloakUserByUsername() {
        // GIVEN
        UserSearchParams params = new UserSearchParams().username("testbackenduser");

        // WHEN
        List<UserRepresentation> users = CLIENT.getUsers(params);

        // THEN
        assertEquals(1, users.size());

        UserRepresentation user = users.get(0);
        assertEquals("testbackenduser", user.getUsername());
        assertEquals(TEST_USER_ID, user.getId());
    }

    @Test
    void shouldCreateUserInKeycloak() {
        // GIVEN
        UserRepresentation user = createUserRep(KOPYTKO54_USER, "kopytko54", "Tomasz", "Wojna",
                "twojna@interia.pl", "3a", "STUDENT");
        UserRepresentation updatedUser = createUserRep(KOPYTKO54_USER, "kopytko54", "Michał", "Stadryniak",
                "twojna@interia.pl", "2g", "TEACHER");

        // CREATE THE USER
        boolean result = CLIENT.createUser(user);

        // USER SHOULD BE CREATED IN KEYCLOAK
        assertTrue(result);

        Optional<UserRepresentation> someUser = CLIENT.getUser("a43856df-96bf-4747-b947-0b2b127ae677");
        if(someUser.isPresent()) {
           Map<String, List<String>> Atrybuty = someUser.get().getAttributes();
           List<String> ROLA = Atrybuty.get("role");
           List<String> GRUPA = Atrybuty.get("group");
            assertNotNull(ROLA);
            assertNotNull(GRUPA);
        }


        UserSearchParams params = new UserSearchParams().username(KOPYTKO54_USER);
        UserRepresentation createdUser = CLIENT.getUsers(params).get(0);


        assertUsersAreEqual(user, createdUser);

        // UPDATE USER DETAILS
        result = CLIENT.updateUser(createdUser.getId(), updatedUser);

        // THE USER DETAILS SHOULD BE UPDATED
        assertTrue(result);
        UserRepresentation updatedSavedUser = CLIENT.getUser(createdUser.getId()).get();
        assertUsersAreEqual(updatedUser, updatedSavedUser);

        // DELETE THE USER
        result = CLIENT.deleteUser(createdUser.getId());
        assertTrue(result);

        // THE USER SHOULD BE DELETED
        Optional<UserRepresentation> deletedUser = CLIENT.getUser(createdUser.getId());
        assertFalse(deletedUser.isPresent());
    }

    private UserRepresentation createUserRep(String username, String password, String firstName, String lastName,
                                             String email, String group, String role) {
        UserRepresentation userRep = new UserRepresentation();
        userRep.setEmail(email);
        userRep.setUsername(username);
        userRep.setFirstName(firstName);
        userRep.setLastName(lastName);
        userRep.setCredentials(Collections.singletonList(getPasswordCredential(password)));

        Map<String, List<String>> customAttributes = new HashMap<>();
        customAttributes.put("role", Collections.singletonList(role));
        customAttributes.put("group", Collections.singletonList(group));

        userRep.setAttributes(customAttributes);
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
