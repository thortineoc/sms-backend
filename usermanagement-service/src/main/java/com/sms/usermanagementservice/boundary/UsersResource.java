package com.sms.usermanagementservice.boundary;

import com.sms.clients.KeycloakClient;
import com.sms.usermanagement.UserDTO;
import com.sms.usermanagementservice.control.UserMapper;
import com.sms.usermanagementservice.entity.User;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@Scope("request")
public class UsersResource {

    @Autowired
    private KeycloakClient keycloakClient;

    @Autowired
    private UsersService usersService;

    @PostMapping("/new-user")
    public ResponseEntity<String> newUser(@RequestBody UserDTO data) {
        User user = UserMapper.toUser(data);
        UserRepresentation userRepresentation = UserMapper.toUserRepresentation(user, usersService.calculatePassword(user));

        if(keycloakClient.createUser(userRepresentation)){
            return new ResponseEntity<>("User Created!", HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>("Cannot create user!", HttpStatus.BAD_REQUEST);
    }
}