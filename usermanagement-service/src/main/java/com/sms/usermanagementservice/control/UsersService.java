package com.sms.usermanagementservice.control;

<<<<<<<<< Temporary merge branch 1

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

=========
import com.sms.clients.KeycloakClient;
import com.sms.clients.entity.UserSearchParams;
import com.sms.usermanagement.UserDTO;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Scope("request")
public class UsersService {

    @Autowired
    private KeycloakClient keycloakClient;

    public void createStudentWithParent(UserDTO user) {
        createUser(user);

        UserSearchParams params = new UserSearchParams().username(calculateUsername(user));
        UserRepresentation createdStudent = keycloakClient.getUsers(params)
                .stream().findFirst().orElseThrow(() -> new IllegalStateException("User was not created"));

        createParent(user, createdStudent);
    }

    public void createUser(UserDTO user) {
        UserRepresentation userRep = UserMapper
                .toUserRepresentation(user, calculateUsername(user), calculatePassword(user));

        if (!keycloakClient.createUser(userRep)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
    }

    private void createParent(UserDTO user, UserRepresentation createdStudent) {

        UserRepresentation parent = UserMapper
                .toParentRepresentationFromStudent(user, calculateUsername(user), calculatePassword(user));
        Map<String, List<String>> parentAttributes = new HashMap<>(parent.getAttributes());
        parentAttributes.put("relatedUser", Collections.singletonList(createdStudent.getId()));
        parent.setAttributes(parentAttributes);

        if (!keycloakClient.createUser(parent)) {
            keycloakClient.deleteUser(createdStudent.getId());
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }

        updateStudentRelatedUser(createdStudent, calculateUsername(user));
    }

    private void updateStudentRelatedUser(UserRepresentation createdStudent, String parentUsername) {

        UserSearchParams params = new UserSearchParams().username(parentUsername);
        UserRepresentation createdParent = keycloakClient.getUsers(params)
                .stream().findFirst().orElseThrow(() -> new IllegalStateException("User was not created"));

        Map<String, List<String>> studentAttributes = new HashMap<>(createdStudent.getAttributes());
        studentAttributes.put("relatedUser", Collections.singletonList(createdParent.getId()));
        createdStudent.setAttributes(studentAttributes);

        if (!keycloakClient.updateUser(createdStudent.getId(), createdStudent)) {
            keycloakClient.deleteUser(createdStudent.getId());
            keycloakClient.deleteUser(createdParent.getId());
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
    }

    private String calculatePassword(UserDTO user) {
        return user.getFirstName().substring(0, Math.min(user.getFirstName().length(), 4)) +
                user.getLastName().substring(0, Math.min(user.getLastName().length(), 4));
    }

    private String calculateUsername(UserDTO user) {
        switch (user.getRole()) {
            case STUDENT: return "s_" + user.getPesel();
            case ADMIN: return "a_" + user.getPesel();
            case TEACHER: return "t_" + user.getPesel();
            case PARENT: return "p_" + user.getPesel();
            default: throw new IllegalStateException();
        }
    }
}
>>>>>>>>> Temporary merge branch 2
