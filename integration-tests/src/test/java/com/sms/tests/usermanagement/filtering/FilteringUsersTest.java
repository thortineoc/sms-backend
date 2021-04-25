package com.sms.tests.usermanagement.filtering;

import com.google.common.collect.Lists;
import com.sms.clients.KeycloakClient;
import com.sms.clients.WebClient;
import com.sms.clients.entity.UserSearchParams;
import com.sms.usermanagement.CustomAttributesDTO;
import com.sms.usermanagement.UserDTO;
import com.sms.usermanagement.UsersFiltersDTO;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.HttpStatus;

import javax.ws.rs.core.MediaType;
import java.util.List;


public class FilteringUsersTest {


    private final static WebClient CLIENT = new WebClient("smsadmin", "smsadmin");
    private final static KeycloakClient KEYCLOAK_CLIENT = new KeycloakClient();


    @BeforeEach
    @AfterEach
    public void cleanup() {
        UserSearchParams params = new UserSearchParams().firstName("firstName");
        List<UserRepresentation> createdUsers = KEYCLOAK_CLIENT.getUsers(params);

        createdUsers.stream().map(UserRepresentation::getId).forEach(KEYCLOAK_CLIENT::deleteUser);
    }


    //GIVEN
    @Test
    void shouldFindAllUsers() {

        //CREATE FILTERS DTO
        UsersFiltersDTO usersFiltersDTO = createRoleAndNameFilter();

        Response response = CLIENT.request("usermanagement-service").get("/users/filter");
        Assertions.assertEquals(HttpStatus.OK.value(), response.statusCode());

        CLIENT.request("usermanagement-service")
                .contentType(MediaType.APPLICATION_JSON)
                .body(usersFiltersDTO)
                .post("/users/filter")
                .then()
                .statusCode(HttpStatus.ACCEPTED.value());

        CLIENT.request("usermanagement-service")
                .contentType(MediaType.APPLICATION_JSON)
                .post("/users/filter")
                .then()
                .statusCode(HttpStatus.ACCEPTED.value());

        usersFiltersDTO = createNoMatchFilter();
        CLIENT.request("usermanagement-service")
                .contentType(MediaType.APPLICATION_JSON)
                .body(usersFiltersDTO)
                .post("/users/filter")
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());
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

    UsersFiltersDTO createRoleAndNameFilter() {
        return UsersFiltersDTO.builder()
                .role("STUDENT")
                .firstName("Tomasz")
                .build();
    }

    UsersFiltersDTO createEmptyFilter() {
        return UsersFiltersDTO.builder()
                .build();
    }

    UsersFiltersDTO createNoMatchFilter() {
        return UsersFiltersDTO.builder()
                .role("ADMIN")
                .username("testbackenduser")
                .firstName("Mateusz")
                .build();
    }
}
