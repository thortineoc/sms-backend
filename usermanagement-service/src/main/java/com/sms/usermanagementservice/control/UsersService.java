package com.sms.usermanagementservice.control;


import com.sms.clients.KeycloakClient;
import com.sms.usermanagement.UserDTO;
import com.sms.usermanagementservice.entity.User;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import java.util.Collections;
import java.util.Optional;

import static com.sms.usermanagementservice.control.UserMapper.toDTO;


@Component
@Scope("request")

public class UsersService {

    UsersService() { }

    public static void getUserById(String id) {
        final KeycloakClient client = new KeycloakClient();
        Optional<UserRepresentation> user = client.getUser(id);
        if(user.isPresent()) {

            User USER = new User.Builder()
                    .firstName(user.get().getFirstName())
                    .lastName(user.get().getLastName())
                    .username(user.get().getUsername())
                    .role(UserDTO.Role.STUDENT)
                    .userAttributes(Collections.emptyMap()) //bez dodatkowych póki co
                    .email(Optional.ofNullable(user.get().getEmail()))
                    .build();
            toDTO(USER);
        }

    }

    public static void getGroup(String groupID) {


    }

    public static void getTeachers() {


    }

    public static void getTeacher(String id) {

    }

    public static void getUsersByRole(String role) {

    }


}
