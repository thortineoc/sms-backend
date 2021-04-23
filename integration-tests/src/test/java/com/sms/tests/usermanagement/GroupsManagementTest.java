package com.sms.tests.usermanagement;

import com.sms.clients.KeycloakClient;
import com.sms.clients.WebClient;
import com.sms.clients.entity.UserSearchParams;
import com.sms.usermanagement.CustomAttributesDTO;
import com.sms.usermanagement.UserDTO;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.HttpStatus;

import javax.ws.rs.core.MediaType;
import java.util.List;


public class GroupsManagementTest {

    private final static WebClient CLIENT = new WebClient("smsadmin", "smsadmin");
    private final static KeycloakClient KEYCLOAK_CLIENT = new KeycloakClient();

    @BeforeAll
    @AfterAll
    static void cleanup(){
        CLIENT.request("usermanagement-service")
                .delete("/groups/temp_group1");


        CLIENT.request("usermanagement-service")
                .delete("/groups/temp_group2");


        CLIENT.request("usermanagement-service")
                .delete("/groups/temp_group3");

        UserSearchParams params = new UserSearchParams().firstName("temp_user_group_test");
        List<UserRepresentation> createdUsers = KEYCLOAK_CLIENT.getUsers(params);

        createdUsers.stream().map(UserRepresentation::getId).forEach(KEYCLOAK_CLIENT::deleteUser);
    }

    @Test
    void shouldReturnForbiddenWhenNotAdmin() {

        //GIVEN
        WebClient tempWebClient = new WebClient();

        //SHOULD RETURN FORBIDDEN WHEN USER IS NOT AN ADMIN
        tempWebClient.request("usermanagement-service")
                .post("/groups/temp")
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value());

        //SHOULD RETURN FORBIDDEN WHEN USER IS NOT AN ADMIN
        tempWebClient.request("usermanagement-service")
                .delete("/groups/temp")
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void shouldCreateQueryAndDeleteGroups() {

        //CREATE FIRST GROUP
        CLIENT.request("usermanagement-service")
                .post("/groups/temp_group1")
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        //CREATE SECOND GROUP
        CLIENT.request("usermanagement-service")
                .post("/groups/temp_group2")
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        //CREATE THIRD GROUP
        CLIENT.request("usermanagement-service")
                .post("/groups/temp_group3")
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        //FETCH GROUPS
        Response response = CLIENT.request("usermanagement-service").get("/groups");
        Assertions.assertEquals(HttpStatus.OK.value(), response.statusCode());

        //CHECK RESPONSE BODY
        String body = response.getBody().prettyPrint();
        Assertions.assertTrue(body.contains("temp_group1"));
        Assertions.assertTrue(body.contains("temp_group2"));
        Assertions.assertTrue(body.contains("temp_group3"));

        //DELETE FIRST GROUP
        CLIENT.request("usermanagement-service")
                .delete("/groups/temp_group1")
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        //DELETE SECOND GROUP
        CLIENT.request("usermanagement-service")
                .delete("/groups/temp_group2")
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        //DELETE THIRD GROUP
        CLIENT.request("usermanagement-service")
                .delete("/groups/temp_group3")
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

    }

    @Test
    void shouldReturnConflictWhenGroupIsUsed(){

        //CREATE GROUP
        CLIENT.request("usermanagement-service")
                .post("/groups/temp_group1")
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        //CREATE USER
        CustomAttributesDTO attributesDTO = CustomAttributesDTO.builder()
                .group("temp_group1")
                .build();

        UserDTO userDTO = UserDTO.builder()
                .id("null")
                .userName("null")
                .firstName("temp_user_group_test")
                .lastName("lastName")
                .pesel("pesel")
                .role(UserDTO.Role.STUDENT)
                .email("mail@email.com")
                .customAttributes(attributesDTO)
                .build();

        CLIENT.request("usermanagement-service")
                .contentType(MediaType.APPLICATION_JSON)
                .body(userDTO)
                .post("/users")
                .then()
                .statusCode(HttpStatus.OK.value());

        //TRY TO DELETE GROUP
        CLIENT.request("usermanagement-service")
                .delete("/groups/temp_group1")
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());
        //TODO: change to CONFLICT

    }

}
