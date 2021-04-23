package com.sms.tests.usermanagement.subjects;

import com.sms.clients.KeycloakClient;
import com.sms.clients.WebClient;
import com.sms.clients.entity.UserSearchParams;
import com.sms.tests.usermanagement.TestUtils;
import com.sms.usermanagement.UserDTO;
import io.restassured.response.Response;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.keycloak.representations.idm.UserRepresentation;

import javax.ws.rs.core.MediaType;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.sms.tests.usermanagement.TestUtils.TEST_PREFIX;
import static com.sms.tests.usermanagement.TestUtils.USER_MANAGEMENT;
import static org.junit.jupiter.api.Assertions.*;

class SubjectsTest {

    private static final WebClient CLIENT = new WebClient("smsadmin", "smsadmin");
    private static final String SUBJECTS = "subjects";
    private static final KeycloakClient KC_CLIENT = new KeycloakClient();
    private static final String TEST_MATHS_SUBJECT = TEST_PREFIX + "Maths";
    private static final String TEACHER_WITH_SUBJECT = TEST_PREFIX + "teacher-with-subject";
    private static final List<String> TEST_SUBJECTS = Lists.newArrayList(
            TEST_MATHS_SUBJECT,
            TEST_PREFIX + "Physics",
            TEST_PREFIX + "English",
            TEST_PREFIX + "Long Subject Name With Spaces"
    );

    @BeforeAll
    @AfterAll
    static void cleanup() {
        KC_CLIENT.getUsers(new UserSearchParams().firstName(TEACHER_WITH_SUBJECT)).stream()
                .findFirst()
                .map(UserRepresentation::getId)
                .ifPresent(KC_CLIENT::deleteUser);

        TEST_SUBJECTS.forEach(SubjectsTest::deleteSubject);
    }

    @Test
    void subjectsCRUDTest() {
        // CREATE A FEW SUBJECTS
        List<Response> responses = TEST_SUBJECTS.stream()
                .map(this::createSubject)
                .filter(this::isFailed)
                .collect(Collectors.toList());
        assertTrue(responses.isEmpty());

        // CHECK IF SUBJECTS WERE SAVED
        List<String> subjects = getSubjects();
        subjects.containsAll(TEST_SUBJECTS);

        // CREATE USER WITH ONE OF THE SUBJECTS
        Response response = createUser(TEACHER_WITH_SUBJECT, TEST_MATHS_SUBJECT);
        assertFalse(isFailed(response));

        // USER SHOULD HAVE THE SUBJECT ATTRIBUTE
        UserRepresentation user = getUser(TEACHER_WITH_SUBJECT).stream().findFirst()
                .orElseThrow(() -> new IllegalStateException("User doesn't exist!"));
        assertTrue(user.getAttributes().containsKey(SUBJECTS));
        assertTrue(user.getAttributes().get(SUBJECTS).contains(TEST_MATHS_SUBJECT));

        // TRY TO DELETE THE SUBJECT
        Response deleteResponse = deleteSubject(TEST_MATHS_SUBJECT);
        assertTrue(isFailed(deleteResponse));

        List<String> usersWithSubject = Arrays.asList(deleteResponse.as(String[].class));

        // THE ENDPOINT RETURNS THE ID OF THE USER WITH THE SUBJECT ...
        assertEquals(1, usersWithSubject.size());
        assertEquals(user.getId(), usersWithSubject.get(0));

        // ... AND THE SUBJECT WAS NOT DELETED
        List<String> subjectsAfterDeleting = getSubjects();
        assertTrue(subjectsAfterDeleting.contains(TEST_MATHS_SUBJECT));

        // DELETE THE TEST USER AND SUBJECTS
        boolean userDeleted = KC_CLIENT.deleteUser(user.getId());
        boolean subjectsDeleted = TEST_SUBJECTS.stream().map(SubjectsTest::deleteSubject)
                .noneMatch(this::isFailed);

        assertTrue(userDeleted);
        assertTrue(subjectsDeleted);
    }

    private Response createUser(String firstName, String subject) {
        UserDTO user = TestUtils.getTeacherWithSubjectDTO(firstName, subject);
        return CLIENT.request(USER_MANAGEMENT)
                .contentType(MediaType.APPLICATION_JSON)
                .log().body()
                .log().uri()
                .body(user)
                .post("/users");
    }

    private List<UserRepresentation> getUser(String firstName) {
        return KC_CLIENT.getUsers(new UserSearchParams().firstName(firstName));
    }

    private List<String> getSubjects() {
        return Arrays.asList(CLIENT.request(USER_MANAGEMENT)
                .contentType(MediaType.APPLICATION_JSON)
                .log().body()
                .log().uri()
                .get("/subjects")
                .as(String[].class));
    }

    private static Response deleteSubject(String name) {
        String path = "/subjects/" + name;
        return CLIENT.request(USER_MANAGEMENT)
                .contentType(MediaType.APPLICATION_JSON)
                .log().body()
                .log().uri()
                .delete(path);
    }

    private Response createSubject(String name) {
        String path = "/subjects/" + name;
        return CLIENT.request(USER_MANAGEMENT)
                .contentType(MediaType.APPLICATION_JSON)
                .log().body()
                .log().uri()
                .post(path);
    }

    private boolean isFailed(Response response) {
        return response.statusCode() >= 300 || response.getStatusCode() < 200;
    }
}
