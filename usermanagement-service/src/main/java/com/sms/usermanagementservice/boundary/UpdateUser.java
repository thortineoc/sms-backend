package com.sms.usermanagementservice.boundary;

import com.sms.clients.KeycloakClient;
import com.sms.clients.entity.UserSearchParams;
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

    public void updateUser(UserDTO userDTO) {
        UserSearchParams params = new UserSearchPrarams().id(userDTO.getID());

        UserRepresentation userRep = UserMapper
                .toUserRepresentation(user, calculateUsername(user), calculatePassword(user)); //I should get username and pswd somehow
        if (!keycloakClient.updateUser(user.getID(), user)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
    }

/*    public void updateUser(UserRepresentation user) {
        if (!keycloakClient.updateUser(user.getID(), user)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
    }*/
}