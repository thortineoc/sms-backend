package com.sms.tests.grades;

import com.google.common.collect.ImmutableMap;
import com.sms.clients.WebClient;
import com.sms.tests.usermanagement.groups.GroupUtils;
import com.sms.tests.usermanagement.subjects.SubjectUtils;
import com.sms.tests.usermanagement.users.UserUtils;
import com.sms.usermanagement.UserDTO;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.Arrays;

import static com.sms.tests.grades.GradeUtils.*;
import static com.sms.tests.usermanagement.TestUtils.TEST_PREFIX;

class GradesManagementTest {

    private static WebClient FIRST_CLIENT;
    private static WebClient SECOND_CLIENT;
    private static WebClient THIRD_CLIENT;
    private static WebClient FOURTH_CLIENT;

    private static UserDTO FIRST_USER;
    private static UserDTO SECOND_USER;
    private static UserDTO THIRD_USER;
    private static UserDTO FOURTH_USER;

    private static final String MATH = "TEST_Math";
    private static final String PHYSICS = "TEST_Physics";
    private static final String BIOLOGY = "TEST_Biology";

    private static final String GROUP_1A = "TEST_1A";
    private static final String GROUP_2B = "TEST_2B";
    private static final String GROUP_3C = "TEST_3C";

    @BeforeAll
    static void setup() {

        deleteUsersSubjectsAndGroups();

        // SET UP SUBJECTS
        SubjectUtils.createSubject(MATH).then().statusCode(204);
        SubjectUtils.createSubject(PHYSICS).then().statusCode(204);
        SubjectUtils.createSubject(BIOLOGY).then().statusCode(204);

        // SET UP GROUPS
        GroupUtils.createGroup(GROUP_1A).then().statusCode(204);
        GroupUtils.createGroup(GROUP_2B).then().statusCode(204);
        GroupUtils.createGroup(GROUP_3C).then().statusCode(204);

        // SET UP STUDENTS
        UserUtils.createUser(getStudentDTO("John", "Fortnite", GROUP_1A))
                .then().statusCode(204);
        UserUtils.createUser(getStudentDTO("Pawel", "Oleksik", GROUP_2B))
                .then().statusCode(204);
        UserUtils.createUser(getStudentDTO("Zdzislaw", "Onderka", GROUP_1A))
                .then().statusCode(204);
        UserUtils.createUser(getStudentDTO("Tomasz", "Wojna", GROUP_3C))
                .then().statusCode(204);

        FIRST_USER = UserUtils.getUsers(ImmutableMap.of("middleName", TEST_PREFIX + "John"))
                .as(UserDTO[].class)[0];
        SECOND_USER = UserUtils.getUsers(ImmutableMap.of("middleName", TEST_PREFIX + "Pawel"))
                .as(UserDTO[].class)[0];
        THIRD_USER = UserUtils.getUsers(ImmutableMap.of("middleName", TEST_PREFIX + "Zdzislaw"))
                .as(UserDTO[].class)[0];
        FOURTH_USER = UserUtils.getUsers(ImmutableMap.of("middleName", TEST_PREFIX + "Tomasz"))
                .as(UserDTO[].class)[0];

        FIRST_CLIENT = new WebClient(FIRST_USER.getUserName(), "JohnFort");
        SECOND_CLIENT = new WebClient(SECOND_USER.getUserName(), "PaweOlek");
        THIRD_CLIENT = new WebClient(THIRD_USER.getUserName(), "ZdziOnde");
        FOURTH_CLIENT = new WebClient(FOURTH_USER.getUserName(), "TomaWojn");
    }

    @AfterAll
    static void cleanup() {

        if (FIRST_USER != null && SECOND_USER != null && THIRD_USER != null && FOURTH_USER != null) {
            deleteUserGrades(FIRST_USER.getId());
            deleteUserGrades(SECOND_USER.getId());
            deleteUserGrades(THIRD_USER.getId());
            deleteUserGrades(FOURTH_USER.getId());
        }
        deleteUsersSubjectsAndGroups();
    }

    private static void deleteUsersSubjectsAndGroups() {
        SubjectUtils.deleteSubject(MATH);
        SubjectUtils.deleteSubject(PHYSICS);
        SubjectUtils.deleteSubject(BIOLOGY);

        GroupUtils.deleteGroup(GROUP_1A);
        GroupUtils.deleteGroup(GROUP_2B);
        GroupUtils.deleteGroup(GROUP_3C);

        Response response = UserUtils.getUsers(ImmutableMap.of("middleName", TEST_PREFIX));
        if (response.statusCode() == 200) {
            Arrays.stream(response.as(UserDTO[].class)).map(UserDTO::getId).forEach(UserUtils::deleteUser);
        }
    }

    @Test
    void gradesTest() {

        // THE TEACHER GIVES HIS STUDENTS SOME GRADES
        saveGrades(FIRST_USER, MATH, 1, 2, 3, 3.5).forEach(this::isOk);
        saveGrades(FIRST_USER, PHYSICS, 4, 3.5).forEach(this::isOk);

        saveGrades(SECOND_USER, PHYSICS, 1, 3.5).forEach(this::isOk);
        saveGrades(SECOND_USER, BIOLOGY, 1).forEach(this::isOk);

        saveGrades(THIRD_USER, MATH, 5).forEach(this::isOk);
        saveGrades(THIRD_USER, PHYSICS, 4, 5).forEach(this::isOk);
        saveGrades(THIRD_USER, BIOLOGY, 3, 5).forEach(this::isOk);

        saveGrades(FOURTH_USER, MATH, 2, 2, 2).forEach(this::isOk);
        saveGrades(FOURTH_USER, BIOLOGY, 1, 2, 1).forEach(this::isOk);

        // EACH STUDENT CAN SEE THEIR GRADES
        Response response = studentGetGrades(FIRST_CLIENT);
        assertHasGrades(response, MATH, 1, 2, 3, 3.5);
        assertHasGrades(response, PHYSICS, 4, 3.5);
        assertHasNoGrades(response, BIOLOGY);

        response = studentGetGrades(SECOND_CLIENT);
        assertHasNoGrades(response, MATH);
        assertHasGrades(response, PHYSICS, 1, 3.5);
        assertHasGrades(response, BIOLOGY, 1);

        response = studentGetGrades(THIRD_CLIENT);
        assertHasGrades(response, MATH, 5);
        assertHasGrades(response, PHYSICS, 4, 5);
        assertHasGrades(response, BIOLOGY, 3, 5);

        response = studentGetGrades(FOURTH_CLIENT);
        assertHasGrades(response, MATH, 2, 2, 2);
        assertHasNoGrades(response, PHYSICS);
        assertHasGrades(response, BIOLOGY, 1, 2, 1);



        // CLEANUP
        deleteUserGrades(FIRST_USER.getId());
        deleteUserGrades(SECOND_USER.getId());
        deleteUserGrades(THIRD_USER.getId());
        deleteUserGrades(FOURTH_USER.getId());
    }

    private void isOk(Response response) {
        response.then().statusCode(HttpStatus.OK.value());
    }

    private static UserDTO getStudentDTO(String firstName, String lastName, String group) {
        return UserUtils.getUserDTO(firstName, lastName, TEST_PREFIX + firstName,
                firstName + "@" + lastName + ".com",
                group, UserDTO.Role.STUDENT);
    }
}
