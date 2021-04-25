package com.sms.tests.usermanagement.users;

import com.google.common.collect.ImmutableMap;
import com.sms.clients.KeycloakClient;
import com.sms.clients.WebClient;
import com.sms.clients.entity.UserSearchParams;
import com.sms.usermanagement.CustomAttributesDTO;
import com.sms.usermanagement.ImmutableUsersFiltersDTO;
import com.sms.usermanagement.UserDTO;
import com.sms.usermanagement.UsersFiltersDTO;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.HttpStatus;

import javax.ws.rs.core.MediaType;
import java.util.*;
import java.util.stream.Collectors;

import static com.sms.tests.usermanagement.TestUtils.TEST_PREFIX;
import static com.sms.tests.usermanagement.TestUtils.USER_MANAGEMENT;


class FilteringUsersTest {

    private final static WebClient CLIENT = new WebClient("smsadmin", "smsadmin");
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

    private void assertUsersDontExist(Response response, String... notLookingFor) {
        response.then().statusCode(200);
        List<String> savedUsers = Arrays.stream(response.getBody().as(UserDTO[].class))
                .map(UserDTO::getFirstName).collect(Collectors.toList());
        Arrays.stream(notLookingFor)
                .forEach(name -> Assertions.assertFalse(savedUsers.contains(name)));
    }

    private void assertUsersExist(Response response, String... lookingFor) {
        response.then().statusCode(200);
        List<String> savedUsers = Arrays.stream(response.getBody().as(UserDTO[].class))
                .map(UserDTO::getFirstName).collect(Collectors.toList());
        Arrays.stream(lookingFor)
                .forEach(name -> Assertions.assertTrue(savedUsers.contains(name)));
    }

    private Response getUsers(Map<String, String> filters) {
        return CLIENT.request(USER_MANAGEMENT)
                .contentType(MediaType.APPLICATION_JSON)
                .log().all()
                .body(getFilters(filters))
                .post("/users/filter");
    }

    private static Response createUser(String firstName, String lastName, String middleName, String email, String group, UserDTO.Role role) {
        UserDTO user = getUserDTO(firstName, lastName, middleName, email, group, role);
        return CLIENT.request(USER_MANAGEMENT)
                .contentType(MediaType.APPLICATION_JSON)
                .log().all()
                .body(user)
                .post("/users");
    }

    private static UserDTO getUserDTO(String firstName, String lastName, String middleName, String email, String group, UserDTO.Role role) {
        return UserDTO.builder()
                .id("test")
                .userName(TEST_PREFIX + UUID.randomUUID())
                .firstName(firstName)
                .lastName(lastName)
                .customAttributes(CustomAttributesDTO.builder()
                        .group(group)
                        .middleName(middleName)
                        .phoneNumber("123")
                        .build())
                .email(email)
                .pesel(firstName + "_123")
                .role(role)
                .build();
    }

    private UsersFiltersDTO getFilters(Map<String, String> filters) {
        ImmutableUsersFiltersDTO.Builder builder = UsersFiltersDTO.builder();
        Optional.ofNullable(filters.get("role")).ifPresent(builder::role);
        Optional.ofNullable(filters.get("firstName")).ifPresent(builder::firstName);
        Optional.ofNullable(filters.get("middleName")).ifPresent(builder::middleName);
        Optional.ofNullable(filters.get("lastName")).ifPresent(builder::lastName);
        Optional.ofNullable(filters.get("email")).ifPresent(builder::email);
        Optional.ofNullable(filters.get("phoneNumber")).ifPresent(builder::phoneNumber);
        Optional.ofNullable(filters.get("pesel")).ifPresent(builder::pesel);
        Optional.ofNullable(filters.get("group")).ifPresent(builder::group);
        Optional.ofNullable(filters.get("search")).ifPresent(builder::search);
        return builder.build();
    }
}
