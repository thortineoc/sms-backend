package com.sms.tests.usermanagement.users;

import com.google.common.collect.Lists;
import com.sms.clients.KeycloakClient;
import com.sms.clients.WebClient;
import com.sms.clients.entity.UserSearchParams;
import com.sms.usermanagement.CustomAttributesDTO;
import com.sms.usermanagement.UserDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.HttpStatus;
import com.sms.usermanagement.*;

import javax.ws.rs.core.MediaType;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

class UpdateUserTest {
    private final static WebClient CLIENT = new WebClient("smsadmin", "smsadmin");
    private final static KeycloakClient KEYCLOAK_CLIENT = new KeycloakClient();

    @BeforeEach
    @AfterEach
    public void cleanup() {
        UserSearchParams params = new UserSearchParams().lastName("lastName");
        List<UserRepresentation> createdUsers = KEYCLOAK_CLIENT.getUsers(params);
        createdUsers.stream().map(UserRepresentation::getId).forEach(KEYCLOAK_CLIENT::deleteUser);

        params = new UserSearchParams().lastName("newLastName");
        createdUsers = KEYCLOAK_CLIENT.getUsers(params);
        createdUsers.stream().map(UserRepresentation::getId).forEach(KEYCLOAK_CLIENT::deleteUser);
    }

    @Test
    void shouldReturnForbiddenWhenNotAdmin() {
        //GIVEN
        UserDTO user = createUserDTO(UserDTO.Role.STUDENT);

        //CREATE NEW STUDENT
        CLIENT.request("usermanagement-service")
                .contentType(MediaType.APPLICATION_JSON)
                .body(user)
                .post("/users")
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        //FIND IN KEYCLOAK
        UserSearchParams params = new UserSearchParams().firstName("firstName");
        UserRepresentation createdUser = KEYCLOAK_CLIENT.getUsers(params).get(0);


        //GIVEN
        WebClient tempWebClient = new WebClient();

        Response response = CLIENT.request("usermanagement-service")
                .contentType(MediaType.APPLICATION_JSON)
                .log().all()
                .get("/users/"+createdUser.getId());

        user = response.as(UserDTO.class);


        //SHOULD RETURN FORBIDDEN WHEN USER IS NOT AN ADMIN
        tempWebClient.request("usermanagement-service")
                .contentType(MediaType.APPLICATION_JSON)
                .body(user)
                .put("/users/update")
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void shouldReturnBadRequestWhenMissingBody() {

        //SHOULD RETURN BAD_REQUEST WHEN BODY IS MISSING
        CLIENT.request("usermanagement-service")
                .contentType(MediaType.APPLICATION_JSON)
                .put("/users/update")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void shouldUpdateStudent() {
        //GIVEN
        UserDTO user = createUserDTO(UserDTO.Role.STUDENT);

        //CREATE NEW STUDENT
        CLIENT.request("usermanagement-service")
                .contentType(MediaType.APPLICATION_JSON)
                .body(user)
                .post("/users")
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());


        //FIND IN KEYCLOAK
        UserSearchParams params = new UserSearchParams().firstName("firstName");
        UserRepresentation createdUser = KEYCLOAK_CLIENT.getUsers(params).get(0);

        //UPDATE USER
        UserDTO newUser = createNewUserDTO(UserDTO.Role.TEACHER, createdUser.getId());
        CLIENT.request("usermanagement-service")
                .contentType(MediaType.APPLICATION_JSON)
                .body(newUser)
                .put("/users/update")
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        Response response = CLIENT.request("usermanagement-service")
                .contentType(MediaType.APPLICATION_JSON)
                .log().all()
                .get("/users/"+createdUser.getId());

        user = response.as(UserDTO.class);

        //CHECK CHANGES
        Assertions.assertEquals("newFirstName", user.getFirstName());
        Assertions.assertEquals("newLastName", user.getLastName());
        Assertions.assertEquals("newmail@email.com", user.getEmail().get());
        Assertions.assertEquals("pesel", user.getPesel());
        Assertions.assertEquals(UserDTO.Role.STUDENT, user.getRole());

        CustomAttributesDTO attributes = user.getCustomAttributes();

        Assertions.assertEquals("789-987-879", attributes.getPhoneNumber().get());
        Assertions.assertEquals("newMiddleName", attributes.getMiddleName().get());
        Assertions.assertEquals("new-example-group", attributes.getGroup().get());

        //CLEANUP
        KEYCLOAK_CLIENT.deleteUser(createdUser.getId());
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


    UserDTO createNewUserDTO(UserDTO.Role role, String Id) {

        List subjects = Lists.newArrayList("subject3", "subject4");

        CustomAttributesDTO attributesDTO = CustomAttributesDTO.builder()
                .phoneNumber("789-987-879")
                .middleName("newMiddleName")
                .relatedUser("new-example-user")
                .group("new-example-group")
                .subjects(subjects)
                .build();

        UserDTO userDTO = UserDTO.builder()
                .id(Id)
                .userName("null")
                .firstName("newFirstName")
                .lastName("newLastName")
                .pesel("newPESEL")
                .role(role)
                .email("newMail@email.com")
                .customAttributes(attributesDTO)
                .build();

        return userDTO;
    }
}
