package com.sms.tests.timetables;

import com.google.common.collect.ImmutableMap;
import com.sms.api.common.Util;
import com.sms.api.timetables.LessonDTO;
import com.sms.api.timetables.TimetableConfigDTO;
import com.sms.api.usermanagement.UserDTO;
import com.sms.tests.config.ConfigClient;
import com.sms.tests.usermanagement.groups.GroupUtils;
import com.sms.tests.usermanagement.subjects.SubjectUtils;
import com.sms.tests.usermanagement.users.UserUtils;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import java.util.Collections;
import java.util.Random;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TimetableManagementTest {

    private static final TimetablesAdminClient ADMIN = new TimetablesAdminClient();
    private static TimetablesStudentClient STUDENT;
    private static TimetablesTeacherClient FIRST_TEACHER;
    private static TimetablesTeacherClient SECOND_TEACHER;
    private static ConfigClient CONFIG_CLIENT = new ConfigClient();
    private static TimetableConfigDTO OLD_CONFIG = null;

    private static final String GROUP = "TEST_" + getRandomString();
    private static final String MATHS = "TEST_" + getRandomString();
    private static final String PHYSICS = "TEST_" + getRandomString();
    private static final String BIOLOGY = "TEST_" + getRandomString();
    private static final String DUPA = "DUPA_" + getRandomString();

    @BeforeAll
    static void setup() {
        Response configResponse = CONFIG_CLIENT.getConfig();
        if (configResponse.statusCode() == 200) {
            OLD_CONFIG = configResponse.as(TimetableConfigDTO.class);
        }
        CONFIG_CLIENT.saveConfig(getConfig(4));

        GroupUtils.createGroup(GROUP).then().statusCode(204);

        SubjectUtils.createSubject(MATHS).then().statusCode(204);
        SubjectUtils.createSubject(PHYSICS).then().statusCode(204);
        SubjectUtils.createSubject(BIOLOGY).then().statusCode(204);

        STUDENT = createStudent("Tomasz", "Wojna", GROUP);
        FIRST_TEACHER = createTeacher("Pawel", "Oleksik", MATHS, PHYSICS);
        SECOND_TEACHER = createTeacher("Zdzislaw", "Oleksik", BIOLOGY);
    }

    @AfterAll
    static void cleanup() {
        Util.runAll(() -> {
            GroupUtils.deleteGroup(GROUP);
        }, () -> {
            SubjectUtils.deleteSubject(MATHS);
            SubjectUtils.deleteSubject(PHYSICS);
            SubjectUtils.deleteSubject(BIOLOGY);
        }, UserUtils::deleteTestUsers, () -> {
            if (OLD_CONFIG == null) {
                CONFIG_CLIENT.deleteConfig();
            } else {
                CONFIG_CLIENT.saveConfig(OLD_CONFIG);
            }
        }, () -> {
            ADMIN.deleteTimetableForGroup(GROUP);
        });
    }

    @Test
    @Order(1)
    void adminCannotGenerateATimetableWhenTeacherIsAssignedSubjectsTheyDontTeach() {
        ADMIN.generateTimetable(GROUP, ImmutableMap.of(
                FIRST_TEACHER.getId(), ImmutableMap.of(BIOLOGY, 4))).then().statusCode(400);
    }

    @Test
    @Order(3)
    void adminCannotGenerateATimetableWithMoreLessonsThanWouldFit() {
        // lessonCount should be set to 4 meaning 5 * 4 = 20 lessons max
        ADMIN.generateTimetable(GROUP, ImmutableMap.of(
                FIRST_TEACHER.getId(), ImmutableMap.of(MATHS, 6, PHYSICS, 8),
                SECOND_TEACHER.getId(), ImmutableMap.of(BIOLOGY, 7))).then().statusCode(400);
    }

    @Test
    @Order(5)
    void adminCannotGenerateATimetableWhenOneOfTheSubjectsDoesntExist() {
        ADMIN.generateTimetable(GROUP, ImmutableMap.of(
                FIRST_TEACHER.getId(), ImmutableMap.of(DUPA, 5))).then().statusCode(400);
    }

    @Test
    @Order(7)
    void adminCannotGenerateATimetableWhenOneOfTheTeachersDoesntExist() {
        ADMIN.generateTimetable(GROUP, ImmutableMap.of(
                DUPA, ImmutableMap.of(MATHS, 10))).then().statusCode(500);
    }

    @Test
    @Order(9)
    void adminCannotGenerateATimetableForGroupThatDoesntExist() {
        ADMIN.generateTimetable(DUPA, ImmutableMap.of(
                FIRST_TEACHER.getId(), ImmutableMap.of(MATHS, 10))).then().statusCode(400);
    }

    @Test
    @Order(11)
    void adminCanGenerateATimetableForAGroup() {
        new TimetablesAssert(ADMIN.generateTimetable(GROUP, ImmutableMap.of(
                FIRST_TEACHER.getId(), ImmutableMap.of(MATHS, 4, PHYSICS, 6),
                SECOND_TEACHER.getId(), ImmutableMap.of(BIOLOGY, 4))))
                .unwrapTimetable()
                .hasLayout(new byte[][] {
                        {1, 1, 1, 1, 1},
                        {1, 1, 1, 1, 1},
                        {1, 1, 1, 1, 0}})
                .hasLessonsWithTeacher(FIRST_TEACHER.getId(), 10)
                .hasLessonsWithTeacher(SECOND_TEACHER.getId(), 4)
                .hasSubject(MATHS, 4)
                .hasSubject(PHYSICS, 6)
                .hasSubject(BIOLOGY, 4)
                .hasTeachers(FIRST_TEACHER.getId(), SECOND_TEACHER.getId());
    }

    @Test
    @Order(13)
    void studentCanSeeTheirTimetable() {
        new TimetablesAssert(STUDENT.getTimetable())
                .unwrapTimetable()
                .hasLayout(new byte[][] {
                        {1, 1, 1, 1, 1},
                        {1, 1, 1, 1, 1},
                        {1, 1, 1, 1, 0}})
                .hasLessonsWithTeacher(FIRST_TEACHER.getId(), 10)
                .hasLessonsWithTeacher(SECOND_TEACHER.getId(), 4)
                .hasSubject(MATHS, 4)
                .hasSubject(PHYSICS, 6)
                .hasSubject(BIOLOGY, 4)
                .hasTeachers(FIRST_TEACHER.getId(), SECOND_TEACHER.getId());
    }

    @Test
    @Order(15)
    void teachersCanSeeTheirTimetables() {
        new TimetablesAssert(FIRST_TEACHER.getTimetable())
                .unwrapTimetable()
                .hasLessonsWithTeacher(FIRST_TEACHER.getId(), 10)
                .hasLessonsWithTeacher(SECOND_TEACHER.getId(), 0)
                .hasTeachers(FIRST_TEACHER.getId())
                .hasSubject(MATHS, 4)
                .hasSubject(PHYSICS, 6)
                .hasSubject(BIOLOGY, 0);

        new TimetablesAssert(SECOND_TEACHER.getTimetable())
                .unwrapTimetable()
                .hasLessonsWithTeacher(FIRST_TEACHER.getId(), 0)
                .hasLessonsWithTeacher(SECOND_TEACHER.getId(), 4)
                .hasTeachers(SECOND_TEACHER.getId())
                .hasSubject(MATHS, 0)
                .hasSubject(PHYSICS, 0)
                .hasSubject(BIOLOGY, 4);
    }

    @Test
    @Order(17)
    void adminCanDeleteASingleLesson() {
        LessonDTO lesson = new TimetablesAssert(ADMIN.getTimetableForGroup(GROUP))
                .unwrapTimetable()
                .getLessonAt(0, 0);

        ADMIN.deleteLesson(lesson.getId().get()).then().statusCode(204);

        new TimetablesAssert(ADMIN.getTimetableForGroup(GROUP))
                .unwrapTimetable()
                .hasLayout(new byte[][] {
                        {0, 1, 1, 1, 1},
                        {1, 1, 1, 1, 1},
                        {1, 1, 1, 1, 0}});
    }

    @Test
    @Order(19)
    void adminCanDeleteLessonsWithAGivenSubject() {
        ADMIN.deleteClassesWithSubject(PHYSICS).then().statusCode(204);

        new TimetablesAssert(ADMIN.getTimetableForGroup(GROUP))
                .unwrapTimetable()
                .hasLessonsWithTeacher(FIRST_TEACHER.getId(), 4)
                .hasLessonsWithTeacher(SECOND_TEACHER.getId(), 4)
                .hasSubject(MATHS, 4)
                .hasSubject(PHYSICS, 0)
                .hasSubject(BIOLOGY, 4)
                .hasTeachers(FIRST_TEACHER.getId(), SECOND_TEACHER.getId());
    }

    @Test
    @Order(21)
    void adminCanDeleteTimetables() {
        ADMIN.deleteTimetableForGroup(GROUP).then().statusCode(204);

        new TimetablesAssert(ADMIN.getTimetableForGroup(GROUP))
                .unwrapTimetable()
                .hasLayout(new byte[][] {
                        {0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0}});
    }

    private static TimetablesTeacherClient createTeacher(String firstName, String lastName, String... subjects) {
        UserDTO user = UserUtils.getTeacherDTO(firstName, lastName, subjects);
        UserUtils.createUser(user).then().statusCode(204);
        UserDTO savedUser = UserUtils.getUsers(ImmutableMap.of("email", user.getEmail().orElse(""))).as(UserDTO[].class)[0];
        return new TimetablesTeacherClient(savedUser);
    }

    private static TimetablesStudentClient createStudent(String firstName, String lastName, String group) {
        UserDTO user = UserUtils.getStudentDTO(firstName, lastName, group);
        UserUtils.createUser(user).then().statusCode(204);
        UserDTO savedUser = UserUtils.getUsers(ImmutableMap.of("email", user.getEmail().orElse(""))).as(UserDTO[].class)[0];
        return new TimetablesStudentClient(savedUser);
    }

    private static String getRandomString() {
        return new Random().ints('a', 'z' + 1)
                .limit(4)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    private static TimetableConfigDTO getConfig(int lessonCount) {
        return TimetableConfigDTO.builder()
                .config(Collections.emptyList())
                .lessonCount(lessonCount)
                .build();
    }
}
