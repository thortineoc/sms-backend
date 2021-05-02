package com.sms.presenceservice.test;

import com.sms.authlib.UserAuthDTO;
import com.sms.context.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Scope("request")
public class TestResource {

    @Autowired
    UserContext userContext;

    @GetMapping(path = "test/service-client-receive")
    public ResponseEntity<UserAuthDTO> testServiceClient() {
        return ResponseEntity.ok(userContext.toUserAuthDTO());
    }
}
