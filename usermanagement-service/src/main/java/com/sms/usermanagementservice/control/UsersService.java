package com.sms.usermanagementservice.control;

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

        UserRepresentation student = UserMapper.toUserRepresentation(user, calculateStudentUsername(user), calculatePassword(user));

        if (!keycloakClient.createUser(student)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        UserSearchParams params = new UserSearchParams().username(calculateStudentUsername(user));
        UserRepresentation createdStudent = keycloakClient.getUsers(params)
                .stream().findFirst().orElseThrow(() -> new IllegalStateException("User was not created"));

        createParent(user, createdStudent);

    }

    public void createAdmin(UserDTO user) {

        UserRepresentation admin = UserMapper.
                toUserRepresentation(user, calculateAdminUsername(user), calculatePassword(user));

        if (!keycloakClient.createUser(admin)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }

    }

    public void createTeacher(UserDTO user) {

        UserRepresentation teacher = UserMapper.
                toUserRepresentation(user, calculateTeacherUsername(user), calculatePassword(user));

        if (!keycloakClient.createUser(teacher)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }

    }

    private String calculatePassword(UserDTO user) {
        return user.getFirstName().substring(0, Math.min(user.getFirstName().length(), 4)) +
                user.getLastName().substring(0, Math.min(user.getLastName().length(), 4));
    }


    private String calculateStudentUsername(UserDTO user) {
        return "s_" + user.getPesel();
    }

    private String calculateAdminUsername(UserDTO user) {
        return "a_" + user.getPesel();
    }

    private String calculateTeacherUsername(UserDTO user) {
        return "t_" + user.getPesel();
    }

    private String calculateParentUsername(UserDTO user) {
        return "p_" + user.getPesel();
    }

    private void createParent(UserDTO user, UserRepresentation createdStudent) {

        UserRepresentation parent = UserMapper.toParentRepresentationFromStudent(user, calculateParentUsername(user), calculatePassword(user));
        Map<String, List<String>> parentAttributes = new HashMap<>(parent.getAttributes());
        parentAttributes.put("relatedUser", Collections.singletonList(createdStudent.getId()));
        parent.setAttributes(parentAttributes);

        if (!keycloakClient.createUser(parent)) {
            keycloakClient.deleteUser(createdStudent.getId());
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }

        updateStudentRelatedUser(createdStudent, calculateParentUsername(user));

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
}
