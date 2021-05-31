package com.sms.clients;

import com.sms.context.UserContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
public class ServiceClientTest {

    @SpyBean
    ServiceClient client;

    @MockBean
    UserContext userContext;

    @Test
    void shouldCreateValidWebTarget() {
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
