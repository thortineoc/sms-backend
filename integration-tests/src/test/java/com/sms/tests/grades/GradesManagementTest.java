package com.sms.tests.grades;

import com.google.common.collect.ImmutableMap;
import com.sms.clients.WebClient;
import com.sms.grades.GradeDTO;
import com.sms.tests.usermanagement.groups.GroupUtils;
import com.sms.tests.usermanagement.subjects.SubjectUtils;
import com.sms.tests.usermanagement.users.UserUtils;
import com.sms.usermanagement.UserDTO;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.sms.tests.grades.GradeUtils.*;
import static com.sms.tests.usermanagement.TestUtils.TEST_PREFIX;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class GradesManagementTest {

    private static WebClient FIRST_CLIENT;
    private static WebClient SECOND_CLIENT;
    private static WebClient THIRD_CLIENT;
    private static WebClient FOURTH_CLIENT;

    private static WebClient FIRST_PARENT_CLIENT;

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

        UserDTO firstParent = UserUtils.getUsers(ImmutableMap.of(
                "role", "PARENT",
                "lastName", "Fortnite")).as(UserDTO[].class)[0];
        FIRST_PARENT_CLIENT = new WebClient(firstParent.getUserName(), "JohnFort");

        // SET UP TEACHERS
        UserUtils.createUser(getTeacherDTO("first-teacher", MATH, PHYSICS, BIOLOGY)).then().statusCode(204);
        UserDTO teacher = UserUtils.getUsers(ImmutableMap.of("middleName", TEST_PREFIX + "first-teacher"))
                .as(UserDTO[].class)[0];
        WebClient FIRST_TEACHER = new WebClient(teacher.getUserName(), "firsfirs");

        useTeacher(FIRST_TEACHER);
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
    @Order(1)
    void teacherCanAssignGradesToStudents() {

        saveGrades(FIRST_USER, MATH, 1, 2, 3, 3.5).forEach(this::isOk);
        saveGrades(FIRST_USER, PHYSICS, 4, 3.5).forEach(this::isOk);

        saveGrades(SECOND_USER, PHYSICS, 1, 3.5).forEach(this::isOk);
        saveGrades(SECOND_USER, BIOLOGY, 1).forEach(this::isOk);

        saveGrades(THIRD_USER, MATH, 5).forEach(this::isOk);
        saveGrades(THIRD_USER, PHYSICS, 4, 5).forEach(this::isOk);
        saveGrades(THIRD_USER, BIOLOGY, 3, 5).forEach(this::isOk);

        saveGrades(FOURTH_USER, MATH, 2, 2, 2).forEach(this::isOk);
        saveGrades(FOURTH_USER, BIOLOGY, 1, 2, 1).forEach(this::isOk);
    }

    @Test
    @Order(2)
    void studentsCanSeeTheirGrades() {

        GradesAssert ga = new GradesAssert(studentGetGrades(FIRST_CLIENT));
        ga.assertHasGrades(MATH, 1, 2, 3, 3.5);
        ga.assertHasGrades(PHYSICS, 4, 3.5);
        ga.assertHasNoGrades(BIOLOGY);

        ga = new GradesAssert(studentGetGrades(SECOND_CLIENT));
        ga.assertHasNoGrades(MATH);
        ga.assertHasGrades(PHYSICS, 1, 3.5);
        ga.assertHasGrades(BIOLOGY, 1);

        ga = new GradesAssert(studentGetGrades(THIRD_CLIENT));
        ga.assertHasGrades(MATH, 5);
        ga.assertHasGrades(PHYSICS, 4, 5);
        ga.assertHasGrades(BIOLOGY, 3, 5);

        ga = new GradesAssert(studentGetGrades(FOURTH_CLIENT));
        ga.assertHasGrades(MATH, 2, 2, 2);
        ga.assertHasNoGrades(PHYSICS);
        ga.assertHasGrades(BIOLOGY, 1, 2, 1);
    }

    @Test
    @Order(3)
    void parentsCanSeeTheSameGradesAsStudents() {

        GradesAssert ga = new GradesAssert(studentGetGrades(FIRST_PARENT_CLIENT));
        ga.assertHasGrades(MATH, 1, 2, 3, 3.5);
        ga.assertHasGrades(PHYSICS, 4, 3.5);
        ga.assertHasNoGrades(BIOLOGY);
    }

    @Test
    @Order(4)
    void teacherCanSeeStudentsGrades() {

        GradesAssert ga = new GradesAssert(teacherGetGrades(GROUP_1A, MATH));
        ga.assertCanSeeStudents(FIRST_USER, THIRD_USER);
        ga.assertCannotSeeStudents(SECOND_USER, FOURTH_USER);
        ga.assertCanSeeStudentsGrades(FIRST_USER.getId(), 1, 2, 3, 3.5);
        ga.assertCanSeeStudentsGrades(THIRD_USER.getId(), 5);

        ga = new GradesAssert(teacherGetGrades(GROUP_2B, PHYSICS));
        ga.assertCanSeeStudents(SECOND_USER);
        ga.assertCannotSeeStudents(FIRST_USER, THIRD_USER, FOURTH_USER);
        ga.assertCanSeeStudentsGrades(SECOND_USER.getId(), 1, 3.5);

        ga = new GradesAssert(teacherGetGrades(GROUP_3C, BIOLOGY));
        ga.assertCanSeeStudents(FOURTH_USER);
        ga.assertCannotSeeStudents(FIRST_USER, SECOND_USER, THIRD_USER);
        ga.assertCanSeeStudentsGrades(FOURTH_USER.getId(), 1, 2, 1);
    }

    @Test
    @Order(5)
    void teacherCanUpdateGrades() {

        Response response = teacherGetGrades(GROUP_1A, MATH);
        List<GradeDTO> grades = getTeacherGrades(response, FIRST_USER.getId());

        List<GradeDTO> updatedGrades = grades.stream()
                .map(grade -> getGradeDTO(grade, 5.0))
                .collect(Collectors.toList());

        updatedGrades.stream().map(GradeUtils::saveGrade).forEach(this::isOk);

        // STUDENT CAN SEE THE CHANGE
        GradesAssert ga = new GradesAssert(studentGetGrades(FIRST_CLIENT));
        ga.assertHasGrades(MATH, 5, 5, 5, 5);
    }

    @Test
    @Order(6)
    void teacherCanRemoveGrades() {

        Response response = teacherGetGrades(GROUP_1A, MATH);
        List<GradeDTO> grades = getTeacherGrades(response, FIRST_USER.getId());

        Assertions.assertEquals(4, grades.size());
        Long firstGradeId = grades.get(0).getId().get();

        deleteGrade(firstGradeId).then().statusCode(HttpStatus.NO_CONTENT.value());

        // STUDENT CANNOT SEE THE DELETED GRADE
        GradesAssert ga = new GradesAssert(studentGetGrades(FIRST_CLIENT));
        ga.assertHasGrades(MATH, 5, 5, 5);
    }

    @Test
    @Order(7)
    void teacherCanGiveAFinalGrade() {

        Response response = saveFinalGrade(FIRST_USER, PHYSICS, 5.00);
        isOk(response);
        GradeDTO finalGrade = response.as(GradeDTO.class);

        // STUDENT CAN SEE THE FINAL GRADE
        GradesAssert ga = new GradesAssert(studentGetGrades(FIRST_CLIENT));
        ga.assertHasFinalGrade(PHYSICS, finalGrade);
    }

    @Test
    @Order(8)
    void adminCanRemoveAllStudentGrades() {

        deleteUserGrades(FIRST_USER.getId()).then().statusCode(HttpStatus.NO_CONTENT.value());

        // STUDENT HAS ALL GRADES REMOVED
        studentGetGrades(FIRST_CLIENT).then().statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @Order(9)
    void studentGradesAreDeletedWhenTheUserIsDeleted() {

        GradesAssert ga = new GradesAssert(studentGetGrades(SECOND_CLIENT));
        List<Long> gradeIds = ga.getStudentGradeIds(SECOND_USER.getId());

        UserUtils.deleteUser(SECOND_USER.getId()).then().statusCode(HttpStatus.NO_CONTENT.value());

        gradeIds.stream().map(GradeUtils::getGrade).forEach(this::isNoContent);
    }

    @Test
    @Order(10)
    void allGradesAreDeletedWhenTheirSubjectIsDeleted() {

        SubjectUtils.deleteSubject(BIOLOGY).then().statusCode(HttpStatus.NO_CONTENT.value());

        // (first has no grades from test (8), second doesn't exist anymore
        new GradesAssert(studentGetGrades(THIRD_CLIENT)).assertHasNoGrades(BIOLOGY);
        new GradesAssert(studentGetGrades(FOURTH_CLIENT)).assertHasNoGrades(BIOLOGY);
    }

    private void isOk(Response response) {
        response.then().statusCode(HttpStatus.OK.value());
    }

    private void isNoContent(Response response) {
        response.then().statusCode(HttpStatus.NO_CONTENT.value());
    }

    private static UserDTO getStudentDTO(String firstName, String lastName, String group) {
        return UserUtils.getUserDTO(firstName, lastName, TEST_PREFIX + firstName,
                firstName + "@" + lastName + ".com",
                group, UserDTO.Role.STUDENT);
    }

    private static UserDTO getTeacherDTO(String name, String... subjects) {
        return UserUtils.getTeacherDTO(name, name, TEST_PREFIX + name,
                UUID.randomUUID().toString() + "@" + UUID.randomUUID().toString() + ".com", Arrays.asList(subjects));
    }
}
