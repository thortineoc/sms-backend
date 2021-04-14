package com.sms.usermanagementservice.boundary;

import com.sms.clients.KeycloakClient;
import com.sms.usermanagement.UserDTO;
import com.sms.usermanagementservice.control.UserMapper;
import com.sms.usermanagementservice.entity.User;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/create")
public class CreateUser {

    private final static KeycloakClient keyloakClient = new KeycloakClient();

    @PostMapping(path = "/new-user")
    public ResponseEntity<String> newUser(@RequestBody UserDTO data) {
        User user = UserMapper.toUser(data);
        String password = user.getFirstName().substring(0, Math.min(user.getFirstName().length(), 4)) +
                user.getLastName().substring(0, Math.min(user.getLastName().length(), 4));
        UserRepresentation userRepresentation = UserMapper.toUserRepresentation(user, password);

        if(keyloakClient.createUser(userRepresentation)){
            return new ResponseEntity<>("User Created!", HttpStatus.OK);
        }

        return new ResponseEntity<>("Cannot create user!", HttpStatus.BAD_REQUEST);
    }

}
