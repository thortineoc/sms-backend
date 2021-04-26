package com.sms.usermanagementservice.control;

import com.sms.clients.KeycloakClient;
import com.sms.clients.entity.UserSearchParams;
import com.sms.context.UserContext;
import com.sms.usermanagement.UserDTO;
import com.sms.usermanagement.UsersFiltersDTO;
import com.sms.usermanagementservice.entity.CustomFilterParams;
import com.sms.usermanagementservice.entity.KeyCloakFilterParams;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Scope("request")
public class UsersService {

    @Autowired
    KeycloakClient keycloakClient;

    @Autowired
    UserFilteringService userFilteringService;

    @Autowired
    private UserContext context;

    public List<UserDTO> filterUserByParameters(UsersFiltersDTO filterParamsDTO) {
        CustomFilterParams customFilterParams = UserMapper.mapCustomFilterParams(filterParamsDTO);
        KeyCloakFilterParams keyCloakFilterParams = UserMapper.mapKeyCloakFilterParams(filterParamsDTO);
        List<UserRepresentation> userList = userFilteringService.filterByKCParams(keyCloakFilterParams);
        return userFilteringService.customFilteringUsers(userList, customFilterParams);
    }



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
                .toParentRepresentationFromStudent(user, calculateParentUsernameFromStudent(user), calculatePassword(user));
        Map<String, List<String>> parentAttributes = new HashMap<>(parent.getAttributes());
        parentAttributes.put("relatedUser", Collections.singletonList(createdStudent.getId()));
        parent.setAttributes(parentAttributes);

        if (!keycloakClient.createUser(parent)) {
            keycloakClient.deleteUser(createdStudent.getId());
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }

        updateStudentRelatedUser(createdStudent, calculateParentUsernameFromStudent(user));
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
            case STUDENT:
                return "s_" + user.getPesel();
            case ADMIN:
                return "a_" + user.getPesel();
            case TEACHER:
                return "t_" + user.getPesel();
            default:
                throw new IllegalStateException();
        }
    }

    private String calculateParentUsernameFromStudent(UserDTO user) {
        return "p_" + user.getPesel();
    }

    public void deleteUser(String userId) {
        if (context.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        UserRepresentation userRepresentation = keycloakClient.getUser(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        Boolean isDeleted = deleteRelatedUser(userRepresentation);
        if (!(keycloakClient.deleteUser(userId) && isDeleted)) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private Boolean deleteRelatedUser(UserRepresentation userRepresentation) {
        Map<String, List<String>> userAttributes = new HashMap<>(userRepresentation.getAttributes());
        String role = userAttributes.get("role").stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("User without role"));
        if (role.equals("PARENT") || role.equals("STUDENT")) {
            String relatedUserId = userAttributes.get("relatedUser").stream()
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("User does not have related user"));
            return keycloakClient.deleteUser(relatedUserId);
        }
        return true;
    }

        public void updateUser(UserDTO userDTO) {
        //find user
        UserSearchParams params = new UserSearchParams().username(userDTO.getUserName());
        UserRepresentation userRep = keycloakClient.getUsers(params)
                .stream().findFirst().orElseThrow(() -> new IllegalStateException("User does not exist"));

        //set new values
        userRep.setFirstName(userDTO.getFirstName());
        userRep.setLastName(userDTO.getLastName());
        if (userDTO.getEmail().isPresent()) {
            userRep.setEmail(userDTO.getEmail().get());
        } else {
            userRep.setEmail("");
        }
        //nie umiem odczytac atrybutow
/*        Map<String, List<String>> attributes = new HashMap<>(userRep.getAttributes());
        attributes.put("relatedUser", Collections.singletonList(createdStudent.getId()));
        userRep.setAttributes(attributes);*/
        //save in keycloak (?)
        if (!keycloakClient.updateUser(userRep.getId(), userRep)) {
            throw new IllegalStateException();
        }

        //parent - to troche jeszcze nie dziala
/*
        if (userDTO.getCustomAttributes().getRelatedUser() != null) {   //this is intentional - if no related uer, then don't update him
            UserRepresentation related = UserMapper.toParentRepresentationFromStudent(userDTO, null, null);
            if (keycloakClient.updateUser(String.valueOf(userDTO.getCustomAttributes().getRelatedUser()), related)) {
                throw new IllegalStateException();
            }
        }*/
    }

}
