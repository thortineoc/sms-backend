package com.sms.usermanagementservice.boundary;

import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Member;
import java.util.List;

@RestController
@RequestMapping("/create")
public class CreateUser {

    @PostMapping(path = "/new-user")
    public String newUser(@RequestBody NewUserDTO data) {
        return "Hello " + data.getUserFirstName() + " " + data.getUserSecondName() + " " + data.getUserSurname() + " " + data.getToken();
    }

    @PostMapping(path = "/new-users")
    public String newUsers(@RequestBody NewUsersDTO data) {
        StringBuilder output = new StringBuilder();
        for(NewUserDTO user : data.getAllUsers()){
            String str = "Hello " + user.getUserFirstName() + " " + user.getUserSecondName() + " " + user.getUserSurname() + " " + user.getToken();
            output.append(str);
            output.append("\n");
        }

        return output.toString();
    }

}
