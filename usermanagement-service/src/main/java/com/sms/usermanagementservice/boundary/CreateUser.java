package com.sms.usermanagementservice.boundary;

import com.sms.authlib.UserDTO;
import com.sms.clients.KeycloakClient;
import com.sms.usermanagementservice.control.UserMapper;
import com.sms.usermanagementservice.entity.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/create")
public class CreateUser {

    @PostMapping(path = "/new-user")
    public String newUser(@RequestBody UserDTO data) {
        User u = UserMapper.toUser(data);


        return "Hello " + data.getFirstName();

    }

    @PostMapping(path = "/new-users")
    public String newUsers(@RequestBody List<UserDTO> users) {
        StringBuilder output = new StringBuilder();
        for(UserDTO user : users){

        }

        return output.toString();
    }

}
