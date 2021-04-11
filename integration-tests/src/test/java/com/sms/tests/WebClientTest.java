package com.sms.tests;

import com.sms.clients.WebClient;
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
        client.request("grades-service").get("health").then().statusCode(200);
    }
}
