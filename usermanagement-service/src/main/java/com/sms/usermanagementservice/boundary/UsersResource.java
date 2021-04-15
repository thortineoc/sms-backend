package com.sms.usermanagementservice.boundary;

import com.sms.clients.KeycloakClient;
import com.sms.context.UserContext;
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

    @Autowired
    private UserContext userContext;

    @PostMapping("/new-student")
    public ResponseEntity<String> newUser(@RequestBody UserDTO data) {

        if(!userContext.getSmsRole().equals("ADMIN")){
            return ResponseEntity.status(403).build();
        }

        if(data.getRole() != UserDTO.Role.STUDENT){
            return ResponseEntity.badRequest().build();
        }

        UserRepresentation userRepresentation = UserMapper.toUserRepresentation(data, usersService.calculatePassword(data.getFirstName(), data.getLastName()));

        if(keycloakClient.createUser(userRepresentation)){
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.badRequest().build();
    }
}