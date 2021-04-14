package com.sms.presenceservice.test;

import com.sms.authlib.UserAuthDTO;
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
    public ResponseEntity<UserAuthDTO> testServiceClient() {
        return ResponseEntity.ok(userContext.toUserDTO());
    }
}
