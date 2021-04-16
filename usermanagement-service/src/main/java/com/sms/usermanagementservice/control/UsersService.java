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

    @Autowired
    static UserContext userContext;

    private static List<UserDTO> UsersDTO = new ArrayList<>();
    private static Map<String, List<String>> Atributes;
    private final static KeycloakClient CLIENT = new KeycloakClient();
    private final static String SMSROLE = "SMSrole";
    private final static String SMSGROUP = "SMSrole";

    public static void getUserById(String id) {
        Optional<UserRepresentation> user = CLIENT.getUser(id);
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

    public static void getGroup(String ID) {
        List<UserRepresentation> users = CLIENT.getAllUsers();
        if (!users.isEmpty()) {
            for (UserRepresentation user : users) {
                Map<String, List<String>> UserAtribs=user.getAttributes();
                List<String> role = UserAtribs.get(SMSROLE);
                List<String> groupID= UserAtribs.get(SMSGROUP);
                for( String match : groupID){
                    if(match.equals(ID))
                        BuildUser(user, UserDTO.Role.valueOf(role.get(0)), groupID.get(0));
                }
            }
        }
    }


        public static void getUser(String object){
            UserSearchParams params = new UserSearchParams();
            params.search(object);
            List<UserRepresentation> users = CLIENT.getUsers(params);
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


    public static void getRole(String object){
        List<UserRepresentation> users = CLIENT.getAllUsers();
        if (!users.isEmpty()) {
            for (UserRepresentation user : users) {
                Map<String, List<String>> UserAtribs=user.getAttributes();
                List<String> role = UserAtribs.get(SMSROLE);
                List<String> groupID= UserAtribs.get(SMSGROUP);
                for( String match : role){
                    if(match.equals(object))
                        BuildUser(user, UserDTO.Role.valueOf(object), groupID.get(0));
                }
            }
        }
    }


    private static void BuildUser(UserRepresentation user, UserDTO.Role ROLE, String GROUP){
        User USER = new User.Builder()
                .firstName(user.getFirstName())
                .username(user.getUsername())
                .role(ROLE)
                .userAttributes(Collections.emptyMap()) //bez dodatkowych póki co
                .email(Optional.ofNullable(user.getEmail()))
                .build();
        UsersDTO.add(toDTO(USER));
    }


    }

