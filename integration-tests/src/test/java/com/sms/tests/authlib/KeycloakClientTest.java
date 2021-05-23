package com.sms.tests.authlib;

import com.sms.api.authlib.TokenDTO;
import com.sms.clients.Environment;
import com.sms.clients.KeycloakClient;
import com.sms.clients.entity.UserSearchParams;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
class KeycloakClientTest {

    private final static String PREFIX = "INTEGRATION_TESTS_";
    private final static String TEST_USERNAME = (PREFIX + UUID.randomUUID().toString()).toLowerCase();

    private static final KeycloakClient CLIENT = new KeycloakClient(Environment.haproxyUrl, Environment.realmName);

    @BeforeAll
    @AfterAll
    static void cleanup() {
        UserSearchParams params = new UserSearchParams().username(TEST_USERNAME);
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
    void shouldCreateUserInKeycloak() {
        // GIVEN
        UserRepresentation user = createUserRep(TEST_USERNAME, "kopytko54", "Tomasz", "Wojna",
                TEST_USERNAME + "@" + TEST_USERNAME, "3a", "STUDENT");
        UserRepresentation updatedUser = createUserRep(TEST_USERNAME, "kopytko54", "Michał", "Stadryniak",
                TEST_USERNAME + "@" + TEST_USERNAME, "2g", "TEACHER");

        // CREATE THE USER
        boolean result = CLIENT.createUser(user);

        // USER SHOULD BE CREATED IN KEYCLOAK
        assertTrue(result);

        UserSearchParams params = new UserSearchParams().username(TEST_USERNAME);
        UserRepresentation createdUser = CLIENT.getUsers(params).get(0);
        assertUsersAreEqual(user, createdUser);

        UserRepresentation userQueriedById = CLIENT.getUser(createdUser.getId()).get();
        assertUsersAreEqual(user, userQueriedById);

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
