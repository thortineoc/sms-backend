package com.sms.homeworkservice.test.boundary;

import com.sms.api.authlib.UserAuthDTO;
import com.sms.clients.ServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.core.MediaType;

@RestController
@Scope("request")
public class TestResource {

    @Autowired
    ServiceClient client;

    @GetMapping(path = "/test/service-client-send")
    public ResponseEntity<UserAuthDTO> testServiceClient() {
        UserAuthDTO user = client.target("presence-service")
                .path("test")
                .path("service-client-receive")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get(UserAuthDTO.class);
        return ResponseEntity.ok(user);
    }
}
