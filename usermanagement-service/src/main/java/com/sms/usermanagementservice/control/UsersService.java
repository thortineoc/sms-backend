package com.sms.usermanagementservice.control;


import com.sms.clients.KeycloakClient;
import com.sms.clients.entity.UserSearchParams;
import com.sms.context.UserContext;
import com.sms.usermanagement.UserDTO;
import com.sms.usermanagementservice.entity.User;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.sms.usermanagementservice.control.UserMapper.toDTO;


@Component
@Scope("request")

public class UsersService {

    private static List<UserDTO> UsersDTO = new ArrayList<>();
    private static Map<String, List<String>> Atributes;

    @Autowired
    static UserContext userContext;

    UsersService() { }

    public static void getUserById(String id) {
        final KeycloakClient client = new KeycloakClient();
        Optional<UserRepresentation> user = client.getUser(id);
        if (user.isPresent()) {

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
        final KeycloakClient client = new KeycloakClient();
        UserSearchParams params = new UserSearchParams();
        params.search(groupID);
        List<UserRepresentation> users = client.getUsers(params);
        if (!users.isEmpty()) {
            for (UserRepresentation user : users) {
                User USER = new User.Builder()
                        .firstName(user.getFirstName())
                        .username(user.getUsername())
                        .role(UserDTO.Role.STUDENT)
                        .userAttributes(Collections.emptyMap()) //bez dodatkowych póki co
                        .email(Optional.ofNullable(user.getEmail()))
                        .build();
                UsersDTO.add(toDTO(USER));
            }
        }
    }


        public static void getUsersByRole (String role){
            final KeycloakClient client = new KeycloakClient();
            UserSearchParams params = new UserSearchParams();
            params.search(role);
            List<UserRepresentation> users = client.getUsers(params);
            if (!users.isEmpty()) {
                for (UserRepresentation user : users) {
                    User USER = new User.Builder()
                            .firstName(user.getFirstName())
                            .username(user.getUsername())
                            .role(UserDTO.Role.STUDENT)
                            .userAttributes(Collections.emptyMap()) //bez dodatkowych póki co
                            .email(Optional.ofNullable(user.getEmail()))
                            .build();
                    UsersDTO.add(toDTO(USER));
                }
            }
        }
        }

        public static void searchUser(String object){
            final KeycloakClient client = new KeycloakClient();
            UserSearchParams params = new UserSearchParams();
            params.search(object);
            List<UserRepresentation> users = client.getUsers(params);
            if (!users.isEmpty()) {
                for (UserRepresentation user : users) {
                    User USER = new User.Builder()
                            .firstName(user.getFirstName())
                            .username(user.getUsername())
                            .role(UserDTO.Role.STUDENT)
                            .userAttributes(Collections.emptyMap()) //bez dodatkowych póki co
                            .email(Optional.ofNullable(user.getEmail()))
                            .build();
                    UsersDTO.add(toDTO(USER));
                }
            }
        }



    }

