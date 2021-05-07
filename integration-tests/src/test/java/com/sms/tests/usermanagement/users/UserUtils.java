package com.sms.tests.usermanagement.users;

import com.sms.clients.WebClient;
import com.sms.usermanagement.CustomAttributesDTO;
import com.sms.usermanagement.ImmutableUsersFiltersDTO;
import com.sms.usermanagement.UserDTO;
import com.sms.usermanagement.UsersFiltersDTO;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;

import javax.ws.rs.core.MediaType;
import java.util.*;
import java.util.stream.Collectors;

import static com.sms.tests.usermanagement.TestUtils.TEST_PREFIX;
import static com.sms.tests.usermanagement.TestUtils.USER_MANAGEMENT;

public class UserUtils {

    public static final WebClient ADMIN = new WebClient("smsadmin", "smsadmin");

    public static void assertUsersDontExist(Response response, String... notLookingFor) {
        response.then().statusCode(200);
        List<String> savedUsers = Arrays.stream(response.getBody().as(UserDTO[].class))
                .map(UserDTO::getFirstName).collect(Collectors.toList());
        Arrays.stream(notLookingFor)
                .forEach(name -> Assertions.assertFalse(savedUsers.contains(name)));
    }

    public static void assertUsersExist(Response response, String... lookingFor) {
        response.then().statusCode(200);
        List<String> savedUsers = Arrays.stream(response.getBody().as(UserDTO[].class))
                .map(UserDTO::getFirstName).collect(Collectors.toList());
        Arrays.stream(lookingFor)
                .forEach(name -> Assertions.assertTrue(savedUsers.contains(name)));
    }

    public static void assertTeachersAreEqual(UserDTO first, UserDTO second) {
        CustomAttributesDTO firstAttributes = first.getCustomAttributes();
        CustomAttributesDTO secondAttributes = second.getCustomAttributes();

        Assertions.assertEquals(first.getFirstName(), second.getFirstName());
        Assertions.assertEquals(first.getLastName(), second.getLastName());
        Assertions.assertEquals(first.getEmail(), second.getEmail());
        Assertions.assertEquals(first.getPesel(), second.getPesel());
        Assertions.assertEquals(first.getRole(), second.getRole());
        Assertions.assertEquals(firstAttributes.getMiddleName(), secondAttributes.getMiddleName());
        Assertions.assertEquals(firstAttributes.getSubjects(), secondAttributes.getSubjects());
        Assertions.assertEquals(firstAttributes.getPhoneNumber(), secondAttributes.getPhoneNumber());
    }

    public static void assertAdminsAreEqual(UserDTO first, UserDTO second) {
        CustomAttributesDTO firstAttributes = first.getCustomAttributes();
        CustomAttributesDTO secondAttributes = second.getCustomAttributes();

        Assertions.assertEquals(first.getFirstName(), second.getFirstName());
        Assertions.assertEquals(first.getLastName(), second.getLastName());
        Assertions.assertEquals(first.getEmail(), second.getEmail());
        Assertions.assertEquals(first.getPesel(), second.getPesel());
        Assertions.assertEquals(first.getRole(), second.getRole());
        Assertions.assertEquals(firstAttributes.getMiddleName(), secondAttributes.getMiddleName());
        Assertions.assertEquals(firstAttributes.getPhoneNumber(), secondAttributes.getPhoneNumber());
    }

    public static void assertStudentsAreEqual(UserDTO first, UserDTO second) {
        CustomAttributesDTO firstAttributes = first.getCustomAttributes();
        CustomAttributesDTO secondAttributes = second.getCustomAttributes();

        Assertions.assertEquals(first.getFirstName(), second.getFirstName());
        Assertions.assertEquals(first.getLastName(), second.getLastName());
        Assertions.assertEquals(first.getEmail(), second.getEmail());
        Assertions.assertEquals(first.getPesel(), second.getPesel());
        Assertions.assertEquals(first.getRole(), second.getRole());
        Assertions.assertEquals(firstAttributes.getGroup(), secondAttributes.getGroup());
        Assertions.assertEquals(firstAttributes.getMiddleName(), secondAttributes.getMiddleName());
        Assertions.assertEquals(firstAttributes.getPhoneNumber(), secondAttributes.getPhoneNumber());
    }

    public static Response getUser(String userId) {
        return ADMIN.request(USER_MANAGEMENT)
                .contentType(MediaType.APPLICATION_JSON)
                .log().all()
                .get("/users/" + userId);
    }

    public static Response getUsers(Map<String, String> filters) {
        return ADMIN.request(USER_MANAGEMENT)
                .contentType(MediaType.APPLICATION_JSON)
                .log().all()
                .body(getFilters(filters))
                .post("/users/filter");
    }

    public static Response deleteUser(String id) {
        return deleteUser(ADMIN, id);
    }

    public static Response deleteUser(WebClient client, String id) {
        return client.request("usermanagement-service")
                .contentType(MediaType.APPLICATION_JSON)
                .delete("/users/" + id);
    }

    public static UsersFiltersDTO getFilters(Map<String, String> filters) {
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

    public static Response createUser(UserDTO user) {
        return createUser(ADMIN, user);
    }

    public static Response createUser(WebClient client, UserDTO user) {
        return client.request(USER_MANAGEMENT)
                .contentType(MediaType.APPLICATION_JSON)
                .log().all()
                .body(user)
                .post("/users");
    }

    public static Response updateUser(UserDTO user) {
        return updateUser(ADMIN, user);
    }

    public static Response updateUser(WebClient client, UserDTO user) {
        return client.request(USER_MANAGEMENT)
                .contentType(MediaType.APPLICATION_JSON)
                .log().all()
                .body(user)
                .put("/users/update");
    }

    public static Response createUser(String firstName, String lastName, String middleName, String email, String group, UserDTO.Role role) {
        UserDTO user = getUserDTO(firstName, lastName, middleName, email, group, role);
        return createUser(user);
    }

    public static UserDTO getUserDTO(String firstName, String lastName, String middleName, String email, String group, UserDTO.Role role) {
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

    public static UserDTO getTeacherDTO(String firstName, String lastName, String middleName, String email, List<String> subjects) {
        return UserDTO.builder()
                .id("test")
                .userName(TEST_PREFIX + UUID.randomUUID())
                .firstName(firstName)
                .lastName(lastName)
                .customAttributes(CustomAttributesDTO.builder()
                        .subjects(subjects)
                        .middleName(middleName)
                        .phoneNumber("123")
                        .build())
                .email(email)
                .pesel(firstName + "_123")
                .role(UserDTO.Role.TEACHER)
                .build();
    }
}
