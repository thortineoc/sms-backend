package com.sms.tests.authlib;

import com.sms.authlib.UserAuthDTO;
import com.sms.clients.WebClient;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ServiceClientAndUserContextTest {

    private final static WebClient CLIENT = new WebClient();

    @Test
    void shouldReturnUserContextFromAnotherService() {
        // WHEN
        Response response = CLIENT.request("homework-service")
                .get("test/service-client-send");
        UserAuthDTO user = response.getBody().as(UserAuthDTO.class);

        // THEN
        Assertions.assertEquals("testbackenduser", user.getUserName());
    }
}
