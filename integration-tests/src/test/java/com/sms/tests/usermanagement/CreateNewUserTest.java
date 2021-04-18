package com.sms.tests.usermanagement;

import com.google.common.collect.Lists;
import com.sms.clients.KeycloakClient;
import com.sms.clients.WebClient;
import com.sms.clients.entity.UserSearchParams;
import com.sms.usermanagement.CustomAttributesDTO;
import com.sms.usermanagement.UserDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.HttpStatus;

import javax.ws.rs.core.MediaType;
import java.util.List;


public class CreateNewUserTest {

    private final static WebClient CLIENT = new WebClient("smsadmin", "smsadmin");
    private final static KeycloakClient KEYCLOAK_CLIENT = new KeycloakClient();

    @BeforeAll
    public static void cleanup() {
        UserSearchParams params = new UserSearchParams().firstName("firstName");
        List<UserRepresentation> createdUsers = KEYCLOAK_CLIENT.getUsers(params);

        for (UserRepresentation user : createdUsers) {
            KEYCLOAK_CLIENT.deleteUser(user.getId());
        }
    }

    @Test
    void shouldReturnForbiddenWhenNotAdmin() {

        //GIVEN

        WebClient tempWebClient = new WebClient();

        UserDTO user = createUserDTO(UserDTO.Role.TEACHER);

        //SHOULD RETURN FORBIDDEN WHEN USER IS NOT AN ADMIN
        tempWebClient.request("usermanagement-service")
                .contentType(MediaType.APPLICATION_JSON)
                .body(user)
                .post("/users")
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value());

    }

    @Test
    void shouldReturnBadRequestWhenMissingBody() {

        //SHOULD RETURN BAD_REQUEST WHEN BODY IS MISSING

        CLIENT.request("usermanagement-service")
                .contentType(MediaType.APPLICATION_JSON)
                .post("/users")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());

    }

    @Test
    void shouldCreateNewTeacher() {

        //GIVEN
        UserDTO user = createUserDTO(UserDTO.Role.TEACHER);

        //CREATE NEW TEACHER
        CLIENT.request("usermanagement-service")
                .contentType(MediaType.APPLICATION_JSON)
                .body(user)
                .post("/users")
                .then()
                .statusCode(HttpStatus.OK.value());

        //TODO check details of created user with API call
        //TODO delete user with API call

        //DELETE CREATED TEACHER
        UserSearchParams params = new UserSearchParams().firstName("firstName");
        UserRepresentation createdUser = KEYCLOAK_CLIENT.getUsers(params).get(0);

        KEYCLOAK_CLIENT.deleteUser(createdUser.getId());
    }

    @Test
    void shouldCreateNewAdmin() {

        //GIVEN
        UserDTO user = createUserDTO(UserDTO.Role.ADMIN);

        //CREATE NEW ADMIN
        CLIENT.request("usermanagement-service")
                .contentType(MediaType.APPLICATION_JSON)
                .body(user)
                .post("/users")
                .then()
                .statusCode(HttpStatus.OK.value());


        //TODO check details of created user with API call
        //TODO delete user with API call

        //DELETE CREATED ADMIN
        UserSearchParams params = new UserSearchParams().firstName("firstName");
        UserRepresentation createdUser = KEYCLOAK_CLIENT.getUsers(params).get(0);

        KEYCLOAK_CLIENT.deleteUser(createdUser.getId());
    }

    @Test
    void shouldCreateNewStudentWithParent() {

        //GIVEN
        UserDTO user = createUserDTO(UserDTO.Role.STUDENT);

        //CREATE STUDENT WITH PARENT
        CLIENT.request("usermanagement-service")
                .contentType("application/json")
                .body(user)
                .post("/users")
                .then()
                .statusCode(HttpStatus.OK.value());


        //TODO check details of created users with API call
        //TODO delete user with API call

        //DELETE CREATED PARENT AND STUDENT
        UserSearchParams paramsStudent = new UserSearchParams().firstName("firstName");
        UserRepresentation createdStudent = KEYCLOAK_CLIENT.getUsers(paramsStudent).get(0);
        KEYCLOAK_CLIENT.deleteUser(createdStudent.getId());

        UserSearchParams paramsParent = new UserSearchParams().lastName("lastName");
        UserRepresentation createdParent = KEYCLOAK_CLIENT.getUsers(paramsParent).get(0);
        KEYCLOAK_CLIENT.deleteUser(createdParent.getId());
    }

    @Test
    void shouldReturnConflictWhenCreatingTwoUsersWithIdenticalData() {

        //GIVEN
        UserDTO user = createUserDTO(UserDTO.Role.ADMIN);

        //CREATING FIRST USER
        CLIENT.request("usermanagement-service")
                .contentType(MediaType.APPLICATION_JSON)
                .body(user)
                .post("/users")
                .then()
                .statusCode(HttpStatus.OK.value());

        //SHOULD RETURN CONFLICT WHEN CREATE SECOND USER
        CLIENT.request("usermanagement-service")
                .contentType(MediaType.APPLICATION_JSON)
                .body(user)
                .post("/users")
                .then()
                .statusCode(HttpStatus.CONFLICT.value());


        //TODO delete user with API call

        //DELETE FIRST USER
        UserSearchParams params = new UserSearchParams().firstName("firstName");
        UserRepresentation createdUser = KEYCLOAK_CLIENT.getUsers(params).get(0);
        KEYCLOAK_CLIENT.deleteUser(createdUser.getId());

    }

    @Test
    void shouldNotCreateParentWhenCannotCreateStudent() {

        //GIVEN
        UserDTO user = createUserDTO(UserDTO.Role.ADMIN);
        UserDTO student = createUserDTO(UserDTO.Role.STUDENT);

        //CREATING FIRST USER
        CLIENT.request("usermanagement-service")
                .contentType(MediaType.APPLICATION_JSON)
                .body(user)
                .post("/users")
                .then()
                .statusCode(HttpStatus.OK.value());

        //SHOULD RETURN CONFLICT WHEN CREATE SECOND USER
        CLIENT.request("usermanagement-service")
                .contentType(MediaType.APPLICATION_JSON)
                .body(student)
                .post("/users")
                .then()
                .statusCode(HttpStatus.CONFLICT.value());


        //TODO delete user with API call

        //MAKE SURE THERE IS ONLY ONE USER WITH NAME lastName
        UserSearchParams params = new UserSearchParams().lastName("lastName");
        List<UserRepresentation> createdUsers = KEYCLOAK_CLIENT.getUsers(params);
        Assertions.assertEquals(1, createdUsers.size());

        //DELETE CREATED USER
        KEYCLOAK_CLIENT.deleteUser(createdUsers.get(0).getId());

    }

    UserDTO createUserDTO(UserDTO.Role role) {

        List subjects = Lists.newArrayList("subject1", "subject2");

        CustomAttributesDTO attributesDTO = CustomAttributesDTO.builder()
                .phoneNumber("132-234-234")
                .middleName("middleName")
                .relatedUser("example-user")
                .group("example-group")
                .subjects(subjects)
                .build();

        UserDTO userDTO = UserDTO.builder()
                .id("null")
                .userName("null")
                .firstName("firstName")
                .lastName("lastName")
                .pesel("pesel")
                .role(role)
                .email("mail@email.com")
                .customAttributes(attributesDTO)
                .build();

        return userDTO;
    }
}
