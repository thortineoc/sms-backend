package com.sms.usermanagementservice.boundary;

import com.sms.clients.KeycloakClient;
import com.sms.clients.entity.UserSearchParams;
import com.sms.usermanagement.UserDTO;
import com.sms.usermanagementservice.control.UserMapper;
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

    private String calculatePassword(UserDTO user) {
        return user.getFirstName().substring(0, Math.min(user.getFirstName().length(), 4)) +
                user.getLastName().substring(0, Math.min(user.getLastName().length(), 4));
    }

    private String calculateStudentUsername(UserDTO user) {
        return "s_" + user.getCustomAttributes().getPesel();
    }

    private String calculateParentUsername(UserDTO user) {
        return "p_" + user.getCustomAttributes().getPesel();
    }

    public boolean createStudentWithParent(UserDTO user) {
        if (!user.getRole().equals(UserDTO.Role.STUDENT)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        UserRepresentation student = UserMapper.toStudentRepresentation(user, calculatePassword(user));
        student.setUsername(calculateStudentUsername(user));
        keycloakClient.createUser(student);

        UserSearchParams params = new UserSearchParams().username(calculateStudentUsername(user));
        UserRepresentation created_student = keycloakClient.getUsers(params).stream().findFirst().orElseThrow(() -> new IllegalStateException("User was not created"));


        UserRepresentation parent = UserMapper.toParentRepresentationFromStudent(user, calculatePassword(user));
        parent.setUsername(calculateParentUsername(user));

        Map<String, List<String>> parent_attributes = new HashMap<>();
        parent_attributes.put("relatedUser", Collections.singletonList(created_student.getId()));
        parent.setAttributes(parent_attributes);

        keycloakClient.createUser(parent);

        params = new UserSearchParams().username(calculateParentUsername(user));
        UserRepresentation created_parent = keycloakClient.getUsers(params).stream().findFirst().orElseThrow(() -> new IllegalStateException("User was not created"));

        Map<String, List<String>> student_attributes = created_student.getAttributes();
        student_attributes.put("relatedUser", Collections.singletonList(created_parent.getId()));
        parent.setAttributes(student_attributes);

        keycloakClient.updateUser(created_student.getId(), created_student);

        return true;
    }
}
