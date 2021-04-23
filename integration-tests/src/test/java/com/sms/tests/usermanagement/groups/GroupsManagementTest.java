package com.sms.tests.usermanagement.groups;

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
import java.util.Arrays;
import java.util.List;


public class GroupsManagementTest {

    private final static WebClient CLIENT = new WebClient("smsadmin", "smsadmin");
    private final static KeycloakClient KEYCLOAK_CLIENT = new KeycloakClient();
    private static final String TEST_LAST_NAME = "temp_user_group_test";
    private static final String TEST_GROUP_1 = "temp_group1";
    private static final String TEST_GROUP_2 = "temp_group2";
    private static final String TEST_GROUP_3 = "temp_group3";

    @BeforeAll
    @AfterAll
    static void cleanup(){

        deleteGroup(TEST_GROUP_1);
        deleteGroup(TEST_GROUP_2);
        deleteGroup(TEST_GROUP_3);

        UserSearchParams params = new UserSearchParams().lastName(TEST_LAST_NAME);
        List<UserRepresentation> createdUsers = KEYCLOAK_CLIENT.getUsers(params);
        createdUsers.stream().map(UserRepresentation::getId).forEach(KEYCLOAK_CLIENT::deleteUser);
    }

    @Test
    void shouldReturnForbiddenWhenNotAdmin() {

        //GIVEN
        WebClient tempWebClient = new WebClient();

        //SHOULD RETURN FORBIDDEN WHEN USER IS NOT AN ADMIN
        tempWebClient.request("usermanagement-service")
                .post("/groups/" + TEST_GROUP_1)
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value());

        //SHOULD RETURN FORBIDDEN WHEN USER IS NOT AN ADMIN
        tempWebClient.request("usermanagement-service")
                .delete("/groups/" + TEST_GROUP_1)
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void shouldCreateQueryAndDeleteGroups() {

        createGroup(TEST_GROUP_1)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        createGroup(TEST_GROUP_2)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        createGroup(TEST_GROUP_3)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        //FETCH GROUPS
        Response response = CLIENT.request("usermanagement-service").get("/groups");
        Assertions.assertEquals(HttpStatus.OK.value(), response.statusCode());

        //CHECK RESPONSE BODY
        String[] array = response.getBody().as(String[].class);
        List<String> list = Arrays.asList(array);
        Assertions.assertTrue(list.containsAll(Arrays.asList(TEST_GROUP_1, TEST_GROUP_2, TEST_GROUP_3)));

        deleteGroup(TEST_GROUP_1)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        deleteGroup(TEST_GROUP_2)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        deleteGroup(TEST_GROUP_3)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

    }

    @Test
    void shouldReturnConflictWhenGroupIsUsed(){

        createGroup(TEST_GROUP_1)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        //CREATE USER
        CustomAttributesDTO attributesDTO = CustomAttributesDTO.builder()
                .group(TEST_GROUP_1)
                .build();

        UserDTO userDTO = UserDTO.builder()
                .id("null")
                .userName("null")
                .firstName("firstName")
                .lastName(TEST_LAST_NAME)
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
        deleteGroup(TEST_GROUP_1)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());
        //TODO: change to CONFLICT

    }

    private static Response deleteGroup(String name){
        return CLIENT.request("usermanagement-service")
                .delete("/groups/" + name);
    }

    private static Response createGroup(String name){
        return CLIENT.request("usermanagement-service")
                .post("/groups/" + name);
    }


}
