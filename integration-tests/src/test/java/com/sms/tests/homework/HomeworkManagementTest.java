package com.sms.tests.homework;

import com.google.common.collect.ImmutableMap;
import com.sms.api.common.Util;
import com.sms.api.homework.AnswerDTO;
import com.sms.api.homework.FileLinkDTO;
import com.sms.api.homework.SimpleHomeworkDTO;
import com.sms.api.usermanagement.UserDTO;
import com.sms.tests.usermanagement.groups.GroupUtils;
import com.sms.tests.usermanagement.subjects.SubjectUtils;
import com.sms.tests.usermanagement.users.UserUtils;
import io.restassured.response.Response;
import io.restassured.specification.MultiPartSpecification;
import org.junit.jupiter.api.*;

import java.time.LocalDateTime;
import java.util.*;

import static com.sms.tests.usermanagement.TestUtils.TEST_PREFIX;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class HomeworkManagementTest {

    private static final String FIRST_GROUP = "TEST_" + getRandomString();
    private static final String SECOND_GROUP = "TEST_" + getRandomString();

    private static final String FIRST_SUBJECT = "TEST_" + getRandomString();
    private static final String SECOND_SUBJECT = "TEST_" + getRandomString();

    private static TeacherHomeworkClient firstTeacher;
    private static TeacherHomeworkClient secondTeacher;
    private static StudentHomeworkClient firstStudent;
    private static StudentHomeworkClient secondStudent;
    private static StudentHomeworkClient thirdStudent;
    private static final AdminHomeworkClient ADMIN = new AdminHomeworkClient();

    private static final Map<String, AnswerDTO> ANSWERS = new HashMap<>();
    private static final Map<String, FileLinkDTO> FILES = new HashMap<>();

    private static SimpleHomeworkDTO firstHomework;
    private static SimpleHomeworkDTO secondHomework;

    @BeforeAll
    static void setup() {
        // CREATE GROUPS
        GroupUtils.createGroup(FIRST_GROUP).then().statusCode(204);
        GroupUtils.createGroup(SECOND_GROUP).then().statusCode(204);

        // CREATE SUBJECTS
        SubjectUtils.createSubject(FIRST_SUBJECT).then().statusCode(204);
        SubjectUtils.createSubject(SECOND_SUBJECT).then().statusCode(204);

        // CREATE USERS
        firstTeacher = createTeacher("Zdzislaw", FIRST_SUBJECT, SECOND_SUBJECT);
        secondTeacher = createTeacher("Grzegorz", FIRST_SUBJECT);
        firstStudent = createStudent("Mateusz", FIRST_GROUP);
        secondStudent = createStudent("Tomasz", SECOND_GROUP);
        thirdStudent = createStudent("Ela", FIRST_GROUP);

        firstHomework = HomeworkClient.getSimpleHomeworkDTO("Add two numbers", FIRST_GROUP, FIRST_SUBJECT, true,
                LocalDateTime.now().plusDays(10));
        secondHomework = HomeworkClient.getSimpleHomeworkDTO("Write 10 words", SECOND_GROUP, SECOND_SUBJECT, false,
                LocalDateTime.now().minusDays(3));
    }

    @AfterAll
    static void cleanup() {
        Util.runAll(() -> {
            SubjectUtils.deleteSubject(FIRST_SUBJECT);
            SubjectUtils.deleteSubject(SECOND_SUBJECT);
        }, () -> {
            GroupUtils.deleteGroup(FIRST_GROUP);
            GroupUtils.deleteGroup(SECOND_GROUP);
        }, () -> {
            firstHomework.getId().ifPresent(ADMIN::deleteHomework);
            secondHomework.getId().ifPresent(ADMIN::deleteHomework);
        }, () -> {
            ANSWERS.values().stream().map(AnswerDTO::getId).filter(Optional::isPresent).map(Optional::get)
                    .forEach(ADMIN::deleteAnswer);
            FILES.values().stream().map(FileLinkDTO::getId).forEach(ADMIN::deleteFile);
        }, () -> {
            Response response = UserUtils.getUsers(ImmutableMap.of("middleName", TEST_PREFIX));
            if (response.statusCode() == 200) {
                Arrays.stream(response.as(UserDTO[].class)).map(UserDTO::getId).forEach(UserUtils::deleteUser);
            }
        });
    }

    @Order(1)
    @Test
    void teacherCanAssignAHomeworkToAGroupOfStudents() {
        firstHomework = new HomeworkAssert(firstTeacher.updateHomework(firstHomework))
                .unwrapSimpleHomework()
                .canSeeHomework(firstHomework)
                .getRealHomework(firstHomework);

        secondHomework = new HomeworkAssert(secondTeacher.updateHomework(secondHomework))
                .unwrapSimpleHomework()
                .canSeeHomework(secondHomework)
                .getRealHomework(secondHomework);
    }

    @Order(2)
    @Test
    void teacherCanOnlySeeHomeworkHeAssigned() {
        new HomeworkAssert(firstTeacher.getHomeworks())
                .unwrapTeacherHomeworks()
                .canSeeHomework(firstHomework)
                .cannotSeeHomework(secondHomework);

        new HomeworkAssert(secondTeacher.getHomeworks())
                .unwrapTeacherHomeworks()
                .cannotSeeHomework(firstHomework)
                .canSeeHomework(secondHomework);
    }

    @Order(3)
    @Test
    void teacherCanUpdateHomeworkDetails() {
        SimpleHomeworkDTO updated = SimpleHomeworkDTO.builder().from(firstHomework)
                .deadline(LocalDateTime.now().plusDays(20)).build();

        firstHomework = new HomeworkAssert(firstTeacher.updateHomework(updated))
                .unwrapSimpleHomework()
                .canSeeHomework(updated)
                .getRealHomework(updated);
    }

    @Order(4)
    @Test
    void teacherCanUploadFilesUnderTheHomework() {
        MultiPartSpecification file = HomeworkClient.getFile("file_1", "file_1".getBytes());
        FileLinkDTO realFile = new HomeworkAssert(firstTeacher.uploadFile(firstHomework.getId().get(), FileLinkDTO.Type.HOMEWORK, file))
                .unwrapFile()
                .getRealFile("file_1");
        FILES.put("file_1", realFile);
    }

    @Order(5)
    @Test
    void studentsCanSeeTheAssignedHomework() {
        new HomeworkAssert(firstStudent.getHomeworks())
                .unwrapStudentHomeworks()
                .canSeeHomework(firstHomework)
                .cannotSeeHomework(secondHomework);

        new HomeworkAssert(secondStudent.getHomeworks())
                .unwrapStudentHomeworks()
                .cannotSeeHomework(firstHomework)
                .canSeeHomework(secondHomework);
    }

    @Order(6)
    @Test
    void teacherCanDeleteAnAssignment() {
        // secondHomework isn't owned by firstTeacher so he cannot delete it
        firstTeacher.deleteHomework(secondHomework.getId().get()).then().statusCode(403);

        // but secondTeacher can
        secondTeacher.deleteHomework(secondHomework.getId().get()).then().statusCode(204);

        new HomeworkAssert(secondTeacher.getHomeworks()).getResponse().then().statusCode(204);
    }

    @Order(7)
    @Test
    void studentsCanGiveTheirAnswersToTheAssignments() {
        AnswerDTO answer = new HomeworkAssert(firstStudent.createAnswer(firstHomework.getId().get()))
                .unwrapAnswer().getRealAnswer(firstStudent.getUser().getId());
        ANSWERS.put("answer_1", answer);

        answer = new HomeworkAssert(thirdStudent.createAnswer(firstHomework.getId().get()))
                .unwrapAnswer().getRealAnswer(thirdStudent.getUser().getId());
        ANSWERS.put("answer_2", answer);
    }

    @Order(8)
    @Test
    void studentsCanUploadFilesInTheirAnswers() {
        MultiPartSpecification firstFile = HomeworkClient.getFile("file_2", "file_2".getBytes());
        MultiPartSpecification secondFile = HomeworkClient.getFile("file_3", "file_3".getBytes());

        Long firstAnswerId = ANSWERS.get("answer_1").getId().get();
        Long secondAnswerId = ANSWERS.get("answer_2").getId().get();

        FileLinkDTO realFile = new HomeworkAssert(firstStudent.uploadFile(firstAnswerId, FileLinkDTO.Type.ANSWER, firstFile))
                .unwrapFile().getRealFile("file_2");
        FILES.put("file_2", realFile);

        realFile = new HomeworkAssert(thirdStudent.uploadFile(secondAnswerId, FileLinkDTO.Type.ANSWER, secondFile))
                .unwrapFile().getRealFile("file_3");
        FILES.put("file_3", realFile);
    }

    @Order(9)
    @Test // impossible case
    void studentsCannotUploadFilesToOtherStudentsAnswers() {
        MultiPartSpecification file = HomeworkClient.getFile("file_3", "file_3".getBytes());
        Long answerId = ANSWERS.get("answer_1").getId().get();

        new HomeworkAssert(thirdStudent.uploadFile(answerId, FileLinkDTO.Type.ANSWER, file))
                .getResponse().then().statusCode(403);
    }

    @Order(10)
    @Test
    void studentsCanSeeHomeworkDetails() {
        new HomeworkAssert(firstStudent.queryHomeworkDetails(firstHomework.getId().get()))
                .unwrapStudentDetail()
                .canSeeHomework(firstHomework)
                .homeworkHasFiles(firstHomework, "file_1")
                .answerExists(firstStudent.getUser(), ANSWERS.get("answer_1"))
                .answerHasFiles(ANSWERS.get("answer_1"), "file_2");

        new HomeworkAssert(thirdStudent.queryHomeworkDetails(firstHomework.getId().get()))
                .unwrapStudentDetail()
                .canSeeHomework(firstHomework)
                .homeworkHasFiles(firstHomework, "file_1")
                .answerExists(thirdStudent.getUser(), ANSWERS.get("answer_2"))
                .answerHasFiles(ANSWERS.get("answer_2"), "file_3");
    }

    @Order(12)
    @Test
    void teacherCanSeeHomeworkDetails() {
        new HomeworkAssert(firstTeacher.queryHomeworkDetails(firstHomework.getId().get()))
                .unwrapTeacherDetail()
                .canSeeHomework(firstHomework)
                .homeworkHasFiles(firstHomework, "file_1")
                .answerExists(firstStudent.getUser(), ANSWERS.get("answer_1"))
                .answerExists(thirdStudent.getUser(), ANSWERS.get("answer_2"))
                .answerHasFiles(ANSWERS.get("answer_1"), "file_2")
                .answerHasFiles(ANSWERS.get("answer_2"), "file_3");
    }

    @Order(13)
    @Test
    void studentsCanDeleteTheFilesInAnswers() {
        new HomeworkAssert(thirdStudent.deleteFile(FILES.get("file_3").getId()))
                .getResponse().then().statusCode(204);

        new HomeworkAssert(thirdStudent.queryHomeworkDetails(firstHomework.getId().get()))
                .unwrapStudentDetail()
                .canSeeHomework(firstHomework)
                .answerExists(thirdStudent.getUser(), ANSWERS.get("answer_2"))
                .answerHasNoFiles(ANSWERS.get("answer_2"));
    }

    @Order(14)
    @Test
    void studentsCanDeleteTheirAnswers() {
        new HomeworkAssert(thirdStudent.deleteAnswer(ANSWERS.get("answer_2").getId().get()))
                .getResponse().then().statusCode(204);

        new HomeworkAssert(thirdStudent.queryHomeworkDetails(firstHomework.getId().get()))
                .unwrapStudentDetail()
                .canSeeHomework(firstHomework)
                .answerDoesNotExist(thirdStudent.getUser());
    }

    @Order(15)
    @Test
    void teacherCanSeeTheUpdatedAnswers() {
        new HomeworkAssert(firstTeacher.queryHomeworkDetails(firstHomework.getId().get()))
                .unwrapTeacherDetail()
                .canSeeHomework(firstHomework)
                .homeworkHasFiles(firstHomework, "file_1")
                .answerExists(firstStudent.getUser(), ANSWERS.get("answer_1"))
                .answerDoesNotExist(thirdStudent.getUser())
                .answerHasFiles(ANSWERS.get("answer_1"), "file_2");
    }

    @Order(16)
    @Test
    void teacherCannotEditTheHomeworkIfAnswersExist() {
        SimpleHomeworkDTO updated = SimpleHomeworkDTO.builder().from(firstHomework)
                .group("dupa").build();

        new HomeworkAssert(firstTeacher.updateHomework(updated)).getResponse()
                .then().statusCode(400);
    }

    @Order(16)
    @Test
    void teacherCanReviewTheAnswers() {
        AnswerDTO reviewed = AnswerDTO.builder().from(ANSWERS.get("answer_1"))
                .review("this is a review").build();

        AnswerDTO real = new HomeworkAssert(firstTeacher.updateAnswer(reviewed))
                .unwrapAnswer()
                .getRealAnswer(firstStudent.getUser().getId());

        Assertions.assertTrue(real.getReview().isPresent());
        Assertions.assertEquals("this is a review", real.getReview().get());
        ANSWERS.put("answer_1", real);
    }

    @Order(17)
    @Test
    void studentsAndTeachersCanDownloadFiles() {
        Response response = new HomeworkAssert(firstTeacher.downloadFile(FILES.get("file_2").getId())).getResponse();
        response.then().statusCode(200);
        String content = new String(response.asByteArray());

        Assertions.assertEquals("file_2", content);

        response = new HomeworkAssert(firstStudent.downloadFile(FILES.get("file_1").getId())).getResponse();
        response.then().statusCode(200);
        content = new String(response.asByteArray());

        Assertions.assertEquals("file_1", content);
    }

    @Order(18)
    @Test
    void answersAndFilesShouldBeRemovedWhenTheirOwnerIsRemoved() {
        UserUtils.deleteUser(firstStudent.getUser().getId()).then().statusCode(204);

        new HomeworkAssert(firstTeacher.queryHomeworkDetails(firstHomework.getId().get()))
                .unwrapTeacherDetail()
                .canSeeHomework(firstHomework)
                .answerDoesNotExist(firstStudent.getUser());

        new HomeworkAssert(ADMIN.downloadFile(FILES.get("file_2").getId())).getResponse()
                .then().statusCode(204);
    }

    private static String getRandomString() {
        return new Random().ints('a', 'z' + 1)
                .limit(4)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    private static TeacherHomeworkClient createTeacher(String firstName, String... subjects) {
        UserDTO user = UserUtils.getTeacherDTO(firstName, subjects);
        UserUtils.createUser(user).then().statusCode(204);
        UserDTO savedUser = UserUtils.getUsers(ImmutableMap.of("email", user.getEmail().orElse(""))).as(UserDTO[].class)[0];
        return new TeacherHomeworkClient(savedUser);
    }

    private static StudentHomeworkClient createStudent(String firstName, String group) {
        UserDTO user = UserUtils.getStudentDTO(firstName, firstName, group);
        UserUtils.createUser(user).then().statusCode(204);
        UserDTO savedUser = UserUtils.getUsers(ImmutableMap.of("email", user.getEmail().orElse(""))).as(UserDTO[].class)[0];
        return new StudentHomeworkClient(savedUser);
    }
}
