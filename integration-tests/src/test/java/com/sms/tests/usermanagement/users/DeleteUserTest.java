package com.sms.tests.usermanagement.users;

import com.google.common.collect.Lists;
import com.sms.clients.KeycloakClient;
import com.sms.clients.WebClient;
import com.sms.clients.entity.UserSearchParams;
import com.sms.usermanagement.CustomAttributesDTO;
import com.sms.usermanagement.UserDTO;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.HttpStatus;

import javax.ws.rs.core.MediaType;
import java.util.List;

import static com.sms.tests.usermanagement.TestUtils.TEST_PREFIX;

public class DeleteUserTest {
    private final static WebClient CLIENT = new WebClient("smsadmin", "smsadmin");
    private final static KeycloakClient KEYCLOAK_CLIENT = new KeycloakClient();

    @BeforeEach
    @AfterEach
    public void cleanup() {
        UserSearchParams params = new UserSearchParams().lastName(TEST_PREFIX + "lastName");
        List<UserRepresentation> createdUsers = KEYCLOAK_CLIENT.getUsers(params);

        createdUsers.stream().map(UserRepresentation::getId).forEach(KEYCLOAK_CLIENT::deleteUser);
    }

    @Test
    void shouldThrowExceptionOnInvalidId() {
        // DELETE USER
        CLIENT.request("usermanagement-service")
                .contentType(MediaType.APPLICATION_JSON)
                .delete("/users/123123")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void shouldDeleteStudentUser() {
        // GIVEN
        // create new user and validate it's creation
        UserDTO user = createUserDTO(UserDTO.Role.STUDENT);
        createNewUser(user);
        // get user from kc with valid id
        UserSearchParams params = new UserSearchParams().lastName(TEST_PREFIX + "lastName");
        UserRepresentation createdUser = KEYCLOAK_CLIENT.getUsers(params).get(0);

        // DELETE USER
        CLIENT.request("usermanagement-service")
                .contentType(MediaType.APPLICATION_JSON)
                .delete("/users/" + createdUser.getId())
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    void shouldDeleteTeacherUser() {
        // GIVEN
        // create new user and validate it's creation
        UserDTO user = createUserDTO(UserDTO.Role.TEACHER);
        createNewUser(user);
        // get user from kc with valid id
        UserSearchParams params = new UserSearchParams().lastName(TEST_PREFIX + "lastName");
        UserRepresentation createdUser = KEYCLOAK_CLIENT.getUsers(params).get(0);

        // DELETE USER
        CLIENT.request("usermanagement-service")
                .contentType(MediaType.APPLICATION_JSON)
                .delete("/users/" + createdUser.getId())
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    void createNewUser(UserDTO user) {
        CLIENT.request("usermanagement-service")
                .contentType(MediaType.APPLICATION_JSON)
                .body(user)
                .post("/users")
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    UserDTO createUserDTO(UserDTO.Role role) {

        List<String> subjects = Lists.newArrayList("subject1", "subject2");
        CustomAttributesDTO attributesDTO = CustomAttributesDTO.builder()
                .phoneNumber("132-234-234")
                .middleName("middleName")
                .relatedUser("example-user")
                .group("example-group")
                .subjects(subjects)
                .build();

        return UserDTO.builder()
                .id("null")
                .userName("userName")
                .firstName("firstName")
                .lastName(TEST_PREFIX + "lastName")
                .pesel("pesel")
                .role(role)
                .email("mail@email.com")
                .customAttributes(attributesDTO)
                .build();
    }
}