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
@Scope("request")
public class UpdateUser {

    @Autowired
    private KeycloakClient keycloakClient;

    public void updateUser(UserRepresentation user) {
        if (!keycloakClient.updateUser(user.getID(), user)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
    }
}