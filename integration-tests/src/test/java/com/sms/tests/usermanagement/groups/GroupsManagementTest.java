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


    @AfterAll
    @BeforeAll
    static void cleanup(){

        GroupUtils.deleteGroup(TEST_GROUP_1);
        GroupUtils.deleteGroup(TEST_GROUP_2);
        GroupUtils.deleteGroup(TEST_GROUP_3);

        UserSearchParams params = new UserSearchParams().lastName(TEST_LAST_NAME);
        List<UserRepresentation> createdUsers = KEYCLOAK_CLIENT.getUsers(params);
        createdUsers.stream().map(UserRepresentation::getId).forEach(KEYCLOAK_CLIENT::deleteUser);
    }

    @Test
    void shouldReturnForbiddenWhenNotAdmin() {

        //GIVEN
        WebClient tempWebClient = new WebClient();

        //SHOULD RETURN FORBIDDEN WHEN USER IS NOT AN ADMIN
        GroupUtils.createGroup(tempWebClient, TEST_GROUP_1).then().statusCode(HttpStatus.FORBIDDEN.value());

        //SHOULD RETURN FORBIDDEN WHEN USER IS NOT AN ADMIN
        GroupUtils.deleteGroup(tempWebClient, TEST_GROUP_1).then().statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void shouldCreateQueryAndDeleteGroups() {

        GroupUtils.createGroup(TEST_GROUP_1)
                .then().statusCode(HttpStatus.NO_CONTENT.value());
        GroupUtils.createGroup(TEST_GROUP_2)
                .then().statusCode(HttpStatus.NO_CONTENT.value());
        GroupUtils.createGroup(TEST_GROUP_3)
                .then().statusCode(HttpStatus.NO_CONTENT.value());

        //FETCH GROUPS
        Response response = GroupUtils.getGroups();
        response.then().statusCode(HttpStatus.OK.value());

        //CHECK RESPONSE BODY
        List<String> list = Arrays.asList(response.getBody().as(String[].class));
        Assertions.assertTrue(list.containsAll(Arrays.asList(TEST_GROUP_1, TEST_GROUP_2, TEST_GROUP_3)));

        GroupUtils.deleteGroup(TEST_GROUP_1)
                .then().statusCode(HttpStatus.NO_CONTENT.value());
        GroupUtils.deleteGroup(TEST_GROUP_2)
                .then().statusCode(HttpStatus.NO_CONTENT.value());
        GroupUtils.deleteGroup(TEST_GROUP_3)
                .then().statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    void shouldReturnConflictWhenGroupIsUsed(){

        GroupUtils.createGroup(TEST_GROUP_1)
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
                .statusCode(HttpStatus.NO_CONTENT.value());

        //TRY TO DELETE GROUP
        GroupUtils.deleteGroup(TEST_GROUP_1)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());

        //DELETE USER
        UserSearchParams params = new UserSearchParams().lastName(TEST_LAST_NAME);
        List<UserRepresentation> createdUsers = KEYCLOAK_CLIENT.getUsers(params);
        createdUsers.stream().map(UserRepresentation::getId).forEach(KEYCLOAK_CLIENT::deleteUser);

        //TRY TO DELETE GROUP AGAIN
        GroupUtils.deleteGroup(TEST_GROUP_1)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

    }
}
