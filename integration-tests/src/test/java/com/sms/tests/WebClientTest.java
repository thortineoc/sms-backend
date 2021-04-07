package com.sms.tests;

import com.google.common.base.Strings;
import com.sms.clients.WebClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class WebClientTest {

    private static WebClient client;

    @BeforeAll
    static void setup() {
        client = new WebClient();
    }

    @Test
    void healthChecks() {
        client.request("homework-service").get("health").then().statusCode(200);
        client.request("timetable-service").get("health").then().statusCode(200);
        client.request("presence-service").get("health").then().statusCode(200);
    }

    @Test
    void shouldReturnOauth2Token() {
        // WHEN
        String token = client.getAccessToken();

        // THEN
        Assertions.assertFalse(Strings.isNullOrEmpty(token));
    }
}
