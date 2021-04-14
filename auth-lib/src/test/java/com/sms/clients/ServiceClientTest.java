package com.sms.clients;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ServiceClientTest {

    @Test
    void shouldCreateValidWebTarget() {
        // GIVEN
        ServiceClient client = new ServiceClient();

        // WHEN
        ServiceClient.ServiceTarget target = client.overrideHaproxyUrl("http://localhost:8080")
                .target("homework-service")
                .path("first")
                .path("second")
                .queryParam("key", "value");
        String uri = target.getWebTarget().getUri().toString();

        // THEN
        Assertions.assertEquals("http://localhost:8080/homework-service/first/second?key=value", uri);
    }
}
