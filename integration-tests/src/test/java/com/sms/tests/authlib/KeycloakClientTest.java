package com.sms.tests.authlib;

import com.sms.authlib.TokenDTO;
import com.sms.clients.KeycloakClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class KeycloakClientTest {

    private final static KeycloakClient CLIENT = new KeycloakClient();
    private final static String TEST_USER_ID = "a43856df-96bf-4747-b947-0b2b127ae677";

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
        List<String> roles = CLIENT.request().getRoles(TEST_USER_ID);

        // THEN
        Assertions.assertTrue(roles.contains("STUDENT"));
        Assertions.assertTrue(roles.contains("USER"));
    }

    private void assertIsToken(TokenDTO token) {
        Assertions.assertNotNull(token.getAccessToken());
        Assertions.assertNotNull(token.getExpiration());
        Assertions.assertNotNull(token.getRefreshExpiration());
        Assertions.assertNotNull(token.getRefreshToken());
        Assertions.assertNotNull(token.getTokenType());
        Assertions.assertNotNull(token.getNotBeforePolicy());
        Assertions.assertNotNull(token.getSessionState());
        Assertions.assertNotNull(token.getScope());
    }
}
