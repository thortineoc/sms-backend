package com.sms.tests.usermanagement.groups;

import com.google.common.collect.ImmutableMap;
import com.sms.clients.WebClient;
import com.sms.tests.usermanagement.users.UserUtils;
import com.sms.api.usermanagement.UserDTO;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static com.sms.tests.usermanagement.TestUtils.TEST_PREFIX;
import static com.sms.tests.usermanagement.TestUtils.getStudentWithGroupDTO;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GroupsManagementTest {

    private static final String TEST_LAST_NAME = TEST_PREFIX + UUID.randomUUID().toString();
    private static final String TEST_GROUP_1 = "TEST_" + getRandomGroup();
    private static final String TEST_GROUP_2 = "TEST_" + getRandomGroup();
    private static final String TEST_GROUP_3 = "TEST_" + getRandomGroup();

    @AfterAll
    @BeforeAll
    static void cleanup(){

        GroupUtils.deleteGroup(TEST_GROUP_1);
        GroupUtils.deleteGroup(TEST_GROUP_2);
        GroupUtils.deleteGroup(TEST_GROUP_3);

        Response response = UserUtils.getUsers(ImmutableMap.of("lastName", TEST_LAST_NAME,
                "role", "STUDENT"));
        if (response.statusCode() == 200) {
            String id = response.as(UserDTO[].class)[0].getId();
            UserUtils.deleteUser(id);
        }
    }

    @Test
    @Order(1)
    void onlyAdminCanCreateAndDeleteGroups() {
        //GIVEN
        WebClient tempWebClient = new WebClient();

        GroupUtils.createGroup(tempWebClient, TEST_GROUP_1).then().statusCode(HttpStatus.FORBIDDEN.value());
        GroupUtils.deleteGroup(tempWebClient, TEST_GROUP_1).then().statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @Order(2)
    void adminCanCreateGroups() {
        GroupUtils.createGroup(TEST_GROUP_1)
                .then().statusCode(HttpStatus.NO_CONTENT.value());
        GroupUtils.createGroup(TEST_GROUP_2)
                .then().statusCode(HttpStatus.NO_CONTENT.value());
        GroupUtils.createGroup(TEST_GROUP_3)
                .then().statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @Order(3)
    void adminCanSeeAllGroups() {
        Response response = GroupUtils.getGroups();
        response.then().statusCode(HttpStatus.OK.value());

        List<String> list = Arrays.asList(response.getBody().as(String[].class));
        Assertions.assertTrue(list.containsAll(Arrays.asList(TEST_GROUP_1, TEST_GROUP_2, TEST_GROUP_3)));
    }

    @Test
    @Order(4)
    void adminCannotCreateTwoSameGroups() {
        GroupUtils.createGroup(TEST_GROUP_1)
                .then().statusCode(HttpStatus.CONFLICT.value());
    }

    @Test
    @Order(5)
    void adminCanCreateStudentAssignedToAGroup() {
        UserDTO student = getStudentWithGroupDTO(TEST_LAST_NAME, TEST_GROUP_1);
        UserUtils.createUser(student);

        Response response = UserUtils.getUsers(ImmutableMap.of("lastName", TEST_LAST_NAME,
                "role", "STUDENT"));
        assertHasGroup(response, TEST_GROUP_1);
    }

    @Test
    @Order(6)
    void adminCanDeleteAGroup() {
        GroupUtils.deleteGroup(TEST_GROUP_1).then().statusCode(HttpStatus.NO_CONTENT.value());

        Response response = UserUtils.getUsers(ImmutableMap.of("lastName", TEST_LAST_NAME,
                "role", "STUDENT"));
        assertHasNoGroup(response);
    }

    private void assertHasGroup(Response response, String group) {
        response.then().statusCode(HttpStatus.OK.value());

        UserDTO teacher = response.as(UserDTO[].class)[0];
        Assertions.assertTrue(teacher.getCustomAttributes().getGroup().isPresent());
        String userGroup = teacher.getCustomAttributes().getGroup().get();
        Assertions.assertEquals(group, userGroup);
    }

    private void assertHasNoGroup(Response response) {
        response.then().statusCode(HttpStatus.OK.value());

        UserDTO teacher = response.as(UserDTO[].class)[0];
        Assertions.assertFalse(teacher.getCustomAttributes().getGroup().isPresent());
    }

    private static String getRandomGroup() {
        return new Random().ints('a', 'z' + 1)
                .limit(4)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }
}
