package com.sms.tests.usermanagement.users;

import com.google.common.collect.ImmutableMap;
import com.sms.clients.KeycloakClient;
import com.sms.clients.entity.UserSearchParams;
import com.sms.api.usermanagement.UserDTO;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.HttpStatus;

import java.util.*;

import static com.sms.tests.usermanagement.TestUtils.TEST_PREFIX;
import static com.sms.tests.usermanagement.users.UserUtils.*;


class FilteringUsersTest {

    private final static KeycloakClient KEYCLOAK_CLIENT = new KeycloakClient();

    private final static String FIRST_NAME = TEST_PREFIX + UUID.randomUUID();
    private final static String SECOND_NAME = TEST_PREFIX + UUID.randomUUID();
    private final static String THIRD_NAME = TEST_PREFIX + UUID.randomUUID();
    private final static String FOURTH_NAME = TEST_PREFIX + UUID.randomUUID();

    @BeforeAll
    static void setup() {
        createUser(FIRST_NAME, "last1", "middle1", TEST_PREFIX + "first@interia.pl", "Ia", UserDTO.Role.STUDENT)
                .then().statusCode(HttpStatus.NO_CONTENT.value());
        createUser(SECOND_NAME, "last2", "middle2", TEST_PREFIX + "second@gmail.com", "IIa", UserDTO.Role.ADMIN)
                .then().statusCode(HttpStatus.NO_CONTENT.value());
        createUser(THIRD_NAME, "last3", "middle2", TEST_PREFIX + "third@onet.pl", "Ia", UserDTO.Role.TEACHER)
                .then().statusCode(HttpStatus.NO_CONTENT.value());
        createUser(FOURTH_NAME, "last4", "middle4",TEST_PREFIX + "fourth@interia.pl", "IIb", UserDTO.Role.STUDENT)
                .then().statusCode(HttpStatus.NO_CONTENT.value());
    }

    @AfterAll
    static void cleanup() {
        UserSearchParams params = new UserSearchParams().search(TEST_PREFIX);
        List<UserRepresentation> createdUsers = KEYCLOAK_CLIENT.getUsers(params);

        createdUsers.stream().map(UserRepresentation::getId).forEach(KEYCLOAK_CLIENT::deleteUser);
    }

    @Test
    void getUserById() {
        Response response = getUsers(Collections.emptyMap());
        UserDTO expectedUser = Arrays.stream(response.as(UserDTO[].class)).findFirst().get();

        String userId = expectedUser.getId();

        response = getUser(userId);
        UserDTO realUser = response.as(UserDTO.class);
        Assertions.assertEquals(expectedUser, realUser);
    }

    @Test
    void getAllUsers() {
        Response response = getUsers(Collections.emptyMap());
        assertUsersExist(response, FIRST_NAME, SECOND_NAME, THIRD_NAME, FOURTH_NAME);
    }

    @Test
    void getStudentRoleUsers() {
        Response response = getUsers(ImmutableMap.of("role", "STUDENT"));
        assertUsersDontExist(response, SECOND_NAME, THIRD_NAME);
        assertUsersExist(response, FIRST_NAME, FOURTH_NAME);
    }

    @Test
    void getUsersByMiddleName() {
        Response response = getUsers(ImmutableMap.of("middleName", "middle2"));
        assertUsersDontExist(response, FIRST_NAME, FOURTH_NAME);
        assertUsersExist(response, SECOND_NAME, THIRD_NAME);
    }

    @Test
    void getUsersByLastName() {
        Response response = getUsers(ImmutableMap.of("lastName", "last4"));
        assertUsersDontExist(response, FIRST_NAME, SECOND_NAME, THIRD_NAME);
        assertUsersExist(response, FOURTH_NAME);
    }

    @Test
    void getUsersByMiddleAndLastName() {
        Response response = getUsers(ImmutableMap.of(
                "middleName", "middle2",
                "lastName", "last3"
        ));
        assertUsersDontExist(response, FIRST_NAME, SECOND_NAME, FOURTH_NAME);
        assertUsersExist(response, THIRD_NAME);
    }

    @Test
    void getUsersByGroup() {
        Response response = getUsers(ImmutableMap.of("group", "Ia"));
        assertUsersDontExist(response, SECOND_NAME, THIRD_NAME, FOURTH_NAME);
        assertUsersExist(response, FIRST_NAME);
    }

    @Test
    void getNoUsersWithNotMatchingFilter() {
        Response response = getUsers(ImmutableMap.of("role", "PARENT"));
        if (response.getStatusCode() != 204) {
            assertUsersDontExist(response, FIRST_NAME, SECOND_NAME, THIRD_NAME, FOURTH_NAME);
        } else {
            response.then().statusCode(204);
        }
    }

    @Test
    void getUsersBySearchParameter() {
        Response response = getUsers(ImmutableMap.of("search", "interia.pl"));
        assertUsersDontExist(response, SECOND_NAME, THIRD_NAME);
        assertUsersExist(response, FIRST_NAME, FOURTH_NAME);
    }

    @Test
    void getUsersBySearchGroupAndRole() {
        Response response = getUsers(ImmutableMap.of(
                "search", ".pl",
                "group", "Ia",
                "role", "STUDENT"
        ));
        assertUsersDontExist(response, SECOND_NAME, THIRD_NAME, FOURTH_NAME);
        assertUsersExist(response, FIRST_NAME);
    }
}
