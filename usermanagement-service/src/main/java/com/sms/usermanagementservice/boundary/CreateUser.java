package com.sms.usermanagementservice.boundary;

import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Member;

@RestController
@RequestMapping("/create-user")
public class CreateUser {

    @PostMapping(path = "/new-user")
    public String postBody(@RequestBody NewUserDTO data) {
        return "Hello " + data.getUserFirstName() + data.getUserSecondName() + data.getUserSurname() + data.getToken();
    }

}
