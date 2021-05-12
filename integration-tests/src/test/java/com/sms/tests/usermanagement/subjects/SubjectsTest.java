package com.sms.tests.usermanagement.subjects;

import com.google.common.collect.ImmutableMap;
import com.sms.clients.WebClient;
import com.sms.tests.usermanagement.TestUtils;
import com.sms.tests.usermanagement.users.UserUtils;
import com.sms.api.usermanagement.UserDTO;
import io.restassured.response.Response;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.*;
import org.springframework.http.HttpStatus;

import javax.ws.rs.core.MediaType;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.sms.tests.usermanagement.TestUtils.TEST_PREFIX;
import static com.sms.tests.usermanagement.TestUtils.USER_MANAGEMENT;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SubjectsTest {

    private static final WebClient CLIENT = new WebClient("smsadmin", "smsadmin");
    private static final String TEST_MATHS_SUBJECT = TEST_PREFIX + "Maths";
    private static final String TEACHER_WITH_SUBJECT = TEST_PREFIX + UUID.randomUUID().toString();
    private static final List<String> TEST_SUBJECTS = Lists.newArrayList(
            TEST_MATHS_SUBJECT,
            TEST_PREFIX + "Physics",
            TEST_PREFIX + "English",
            TEST_PREFIX + "Long Subject Name With Spaces"
    );

    @BeforeAll
    @AfterAll
    static void cleanup() {
        Response response = UserUtils.getUsers(ImmutableMap.of("firstName", TEACHER_WITH_SUBJECT));
        if (response.statusCode() == 200) {
            String id = response.as(UserDTO[].class)[0].getId();
            UserUtils.deleteUser(id);
        }
        TEST_SUBJECTS.forEach(SubjectUtils::deleteSubject);
    }

    @Test
    @Order(1)
    void adminCanCreateSubjects() {
        List<Response> responses = TEST_SUBJECTS.stream()
                .map(SubjectUtils::createSubject)
                .filter(this::isFailed)
                .collect(Collectors.toList());
        assertTrue(responses.isEmpty());
    }

    @Test
    @Order(2)
    void adminCanSeeAllSubjects() {
        List<String> subjects = Arrays.asList(SubjectUtils.getSubjects().as(String[].class));
        subjects.containsAll(TEST_SUBJECTS);
    }

    @Test
    @Order(3)
    void adminCanCreateTeacherWithAssignedSubject() {
        createUser(TEACHER_WITH_SUBJECT, TEST_MATHS_SUBJECT).then().statusCode(HttpStatus.NO_CONTENT.value());

        Response response = UserUtils.getUsers(ImmutableMap.of("firstName", TEACHER_WITH_SUBJECT));
        assertHasSubject(response, TEST_MATHS_SUBJECT);
    }

    @Test
    @Order(4)
    void adminCanDeleteASubject() {
        SubjectUtils.deleteSubject(TEST_MATHS_SUBJECT).then().statusCode(HttpStatus.NO_CONTENT.value());

        // THE SUBJECT WAS REMOVED FROM TEACHER ATTRIBUTES
        Response response = UserUtils.getUsers(ImmutableMap.of("firstName", TEACHER_WITH_SUBJECT));
        assertDoesNotHaveSubject(response, TEST_MATHS_SUBJECT);
    }

    private void assertHasSubject(Response response, String subject) {
        response.then().statusCode(HttpStatus.OK.value());

        UserDTO teacher = response.as(UserDTO[].class)[0];
        List<String> subjects = teacher.getCustomAttributes().getSubjects();
        assertTrue(subjects.contains(subject));
    }

    private void assertDoesNotHaveSubject(Response response, String subject) {
        response.then().statusCode(HttpStatus.OK.value());

        UserDTO teacher = response.as(UserDTO[].class)[0];
        List<String> subjects = teacher.getCustomAttributes().getSubjects();
        assertFalse(subjects.contains(subject));
    }

    private Response createUser(String firstName, String subject) {
        UserDTO user = TestUtils.getTeacherWithSubjectDTO(firstName, subject);
        return CLIENT.request(USER_MANAGEMENT)
                .contentType(MediaType.APPLICATION_JSON)
                .log().all()
                .body(user)
                .post("/users");
    }

    private boolean isFailed(Response response) {
        return response.statusCode() >= 300 || response.getStatusCode() < 200;
    }
}
