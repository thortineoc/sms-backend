package com.sms.tests.usermanagement.users;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.sms.clients.WebClient;
import com.sms.usermanagement.CustomAttributesDTO;
import com.sms.usermanagement.UserDTO;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import javax.ws.rs.core.MediaType;
import java.util.Arrays;

class UpdateUserTest {

    private final static WebClient CLIENT = new WebClient("smsadmin", "smsadmin");

    @BeforeEach
    @AfterEach
    public void cleanup() {
        Response response = UserUtils.getUsers(ImmutableMap.of("lastName", "lastName"));
        if (response.statusCode() == 200) {
            Arrays.stream(response.as(UserDTO[].class)).map(UserDTO::getId).forEach(UserUtils::deleteUser);
        }

        response = UserUtils.getUsers(ImmutableMap.of("lastName", "newLastName"));
        if (response.statusCode() == 200) {
            Arrays.stream(response.as(UserDTO[].class)).map(UserDTO::getId).forEach(UserUtils::deleteUser);
        }
    }

    @Test
    void shouldReturnForbiddenWhenNotAdmin() {
        //GIVEN
        UserDTO user = createUserDTO(UserDTO.Role.STUDENT);

        //CREATE NEW STUDENT
        UserUtils.createUser(user).then().statusCode(HttpStatus.NO_CONTENT.value());

        //FIND IN KEYCLOAK
        UserDTO createdUser = UserUtils.getUsers(ImmutableMap.of("firstName", "firstName"))
                .as(UserDTO[].class)[0];

        //GIVEN
        WebClient tempWebClient = new WebClient();

        //SHOULD RETURN FORBIDDEN WHEN USER IS NOT AN ADMIN
        UserUtils.updateUser(tempWebClient, createdUser).then().statusCode(HttpStatus.FORBIDDEN.value());
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
        UserUtils.createUser(user).then().statusCode(HttpStatus.NO_CONTENT.value());

        //FIND IN KEYCLOAK
        UserDTO createdUser = UserUtils.getUsers(ImmutableMap.of("firstName", "firstName"))
                .as(UserDTO[].class)[0];

        //UPDATE USER
        UserDTO newUser = createNewUserDTO(UserDTO.Role.TEACHER, createdUser.getId());
        UserUtils.updateUser(newUser).then().statusCode(HttpStatus.NO_CONTENT.value());

        UserDTO updatedUser = UserUtils.getUser(createdUser.getId()).as(UserDTO.class);

        //CHECK CHANGES
        Assertions.assertEquals("newFirstName", updatedUser.getFirstName());
        Assertions.assertEquals("newLastName", updatedUser.getLastName());
        Assertions.assertEquals("newmail@email.com", updatedUser.getEmail().get());
        Assertions.assertEquals("pesel", updatedUser.getPesel());
        Assertions.assertEquals(UserDTO.Role.STUDENT, updatedUser.getRole());

        CustomAttributesDTO attributes = updatedUser.getCustomAttributes();

        Assertions.assertEquals("789-987-879", attributes.getPhoneNumber().get());
        Assertions.assertEquals("newMiddleName", attributes.getMiddleName().get());
        Assertions.assertEquals("new-example-group", attributes.getGroup().get());

        //CLEANUP
        UserUtils.deleteUser(createdUser.getId()).then().statusCode(HttpStatus.NO_CONTENT.value());
    }

    UserDTO createUserDTO(UserDTO.Role role) {
        return UserDTO.builder()
                .id("null")
                .userName("null")
                .firstName("firstName")
                .lastName("lastName")
                .pesel("pesel")
                .role(role)
                .email("mail@email.com")
                .customAttributes(CustomAttributesDTO.builder()
                        .phoneNumber("132-234-234")
                        .middleName("middleName")
                        .relatedUser("example-user")
                        .group("example-group")
                        .subjects(Lists.newArrayList("subject1", "subject2"))
                        .build())
                .build();
    }

    UserDTO createNewUserDTO(UserDTO.Role role, String Id) {
        return UserDTO.builder()
                .id(Id)
                .userName("null")
                .firstName("newFirstName")
                .lastName("newLastName")
                .pesel("newPESEL")
                .role(role)
                .email("newMail@email.com")
                .customAttributes(CustomAttributesDTO.builder()
                        .phoneNumber("789-987-879")
                        .middleName("newMiddleName")
                        .relatedUser("new-example-user")
                        .group("new-example-group")
                        .subjects(Lists.newArrayList("subject3", "subject4"))
                        .build())
                .build();
    }
}
