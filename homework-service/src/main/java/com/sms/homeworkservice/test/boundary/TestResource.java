package com.sms.homeworkservice.test.boundary;

import com.sms.authlib.UserDTO;
import com.sms.clients.ServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.core.MediaType;

@RestController
public class TestResource {

    @Autowired
    ServiceClient client;

    @Value("${haproxy.url}")
    String haproxyUrl;

    @GetMapping(path = "/test/service-client-send")
    public ResponseEntity<UserDTO> testServiceClient() {
        UserDTO user = client.haproxyUrl(haproxyUrl)
                .target("presence-service")
                .path("test")
                .path("service-client-receive")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get(UserDTO.class);
        return ResponseEntity.ok(user);
    }
}
