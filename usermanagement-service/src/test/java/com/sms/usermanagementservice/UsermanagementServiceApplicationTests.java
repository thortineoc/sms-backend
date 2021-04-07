package com.sms.usermanagementservice;

import com.sms.authlib.UserDTO;
import com.sms.context.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class TestResource {

    @Autowired
    UserContext userContext;

    @GetMapping(path = "test/service-client-receive")
    public ResponseEntity<UserDTO> testServiceClient() {
        return ResponseEntity.ok(userContext.toUserDTO());
    }
}