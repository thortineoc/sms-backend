package com.sms.tests.homework;

import com.sms.api.common.Util;
import com.sms.api.homework.*;
import com.sms.api.usermanagement.UserDTO;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.Response;
import org.assertj.core.api.Assertions;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class HomeworkAssert {

    private final Response response;
    private Map<String, Map<String, List<SimpleHomeworkDTO>>> teacherList;
    private Map<String, List<SimpleHomeworkDTO>> studentList;

    // caches
    private final Map<String, AnswerDTO> answers = new HashMap<>();
    private final Map<String, SimpleHomeworkDTO> homeworks = new HashMap<>();
    private final Set<FileLinkDTO> files = new HashSet<>();
    private final Map<String, UserDTO> users = new HashMap<>();

    public HomeworkAssert(Response response) {
        this.response = response;
    }

    public Response getResponse() {
        return response;
    }

    public HomeworkAssert unwrapTeacherHomeworks() {
        response.then().statusCode(200);
        teacherList = response.as(new TypeRef<Map<String, Map<String, List<SimpleHomeworkDTO>>>>() {});
        homeworks.putAll(teacherList.values().stream()
                .map(Map::values).flatMap(Collection::stream)
                .flatMap(Collection::stream)
                .collect(Collectors.toMap(this::homeworkId, Function.identity())));
        return this;
    }

    public HomeworkAssert unwrapStudentHomeworks() {
        response.then().statusCode(200);
        studentList = response.as(new TypeRef<Map<String, List<SimpleHomeworkDTO>>>(){});
        homeworks.putAll(studentList.values().stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toMap(this::homeworkId, Function.identity())));
        return this;
    }

    public HomeworkAssert unwrapTeacherDetail() {
        response.then().statusCode(200);
        HomeworkDTO homework = response.as(HomeworkDTO.class);
        homeworks.put(homeworkId(homework), homework);

        files.addAll(homework.getFiles());
        homework.getAnswers().forEach(hs -> {
           users.put(hs.getStudent().getId(), hs.getStudent());
           hs.getAnswer().ifPresent(a -> answers.put(answerId(a), a));
           hs.getAnswer().map(AnswerDTO::getFiles).ifPresent(files::addAll);
        });
        return this;
    }

    public HomeworkAssert unwrapStudentDetail() {
        response.then().statusCode(200);
        StudentHomeworkDTO homework = response.as(StudentHomeworkDTO.class);
        homeworks.put(homeworkId(homework), homework);

        files.addAll(homework.getFiles());
        homework.getAnswer().ifPresent(a -> {
            files.addAll(a.getFiles());
            answers.put(answerId(a), a);
        });
        return this;
    }

    public HomeworkAssert unwrapAnswer() {
        response.then().statusCode(200);
        AnswerDTO answer = response.as(AnswerDTO.class);
        answers.put(answerId(answer), answer);
        files.addAll(answer.getFiles());
        return this;
    }

    public HomeworkAssert unwrapFile() {
        response.then().statusCode(200);
        FileLinkDTO file = response.as(FileLinkDTO.class);
        files.add(file);
        return this;
    }

    public HomeworkAssert unwrapSimpleHomework() {
        response.then().statusCode(200);
        SimpleHomeworkDTO homework = response.as(SimpleHomeworkDTO.class);
        homeworks.put(homeworkId(homework), homework);
        return this;
    }

    public SimpleHomeworkDTO getRealHomework(SimpleHomeworkDTO homework) {
        return homeworks.get(homeworkId(homework));
    }

    public AnswerDTO getRealAnswer(String studentId) {
        return answers.get(studentId);
    }

    public FileLinkDTO getRealFile(String filename) {
        return files.stream().filter(f -> f.getFilename().equals(filename))
                .findFirst().orElseThrow(RuntimeException::new);
    }

    public HomeworkAssert canSeeHomework(SimpleHomeworkDTO expected) {
        String identifier = homeworkId(expected);
        Assertions.assertThat(homeworks).containsKey(identifier);

        SimpleHomeworkDTO real = homeworks.get(identifier);
        return assertHomeworkDetails(expected, real);
    }

    public HomeworkAssert cannotSeeHomework(SimpleHomeworkDTO expected) {
        String identifier = homeworkId(expected);
        Assertions.assertThat(homeworks).doesNotContainKey(identifier);
        return this;
    }

    public HomeworkAssert homeworkHasFiles(SimpleHomeworkDTO homework, String... filenames) {
        HomeworkDTO real = (HomeworkDTO) homeworks.get(homeworkId(homework));

        Set<String> realFilenames = real.getFiles().stream().map(FileLinkDTO::getFilename).collect(Collectors.toSet());
        Set<String> expectedFilenames = new HashSet<>(Arrays.asList(filenames));

        Assertions.assertThat(realFilenames).isEqualTo(expectedFilenames);
        return this;
    }

    public HomeworkAssert answerExists(UserDTO student, AnswerDTO answer) {
        String identifier = answerId(answer);
        Assertions.assertThat(answers).containsKey(identifier);
        AnswerDTO real = answers.get(identifier);

        assertEquals(student.getId(), real.getStudentId().get());
        assertEquals(answer.getReview(), real.getReview());
        return this;
    }

    public HomeworkAssert answerDoesNotExist(UserDTO student) {
        Assertions.assertThat(answers).doesNotContainKey(student.getId());
        return this;
    }

    public HomeworkAssert answerIsGraded(AnswerDTO answer, Double grade) {
        AnswerDTO real = answers.get(answerId(answer));
        assertTrue(real.getGrade().isPresent());
        assertEquals(BigDecimal.valueOf(grade).setScale(2, RoundingMode.HALF_UP),
                real.getGrade().get().getGrade().setScale(2, RoundingMode.HALF_UP));
        return this;
    }

    public HomeworkAssert answerHasFiles(AnswerDTO answer, String... filenames) {
        AnswerDTO real = answers.get(answerId(answer));

        assertEquals(filenames.length, answer.getFiles().size());
        Set<String> expected = new HashSet<>(Arrays.asList(filenames));
        Set<String> realFiles = real.getFiles().stream().map(FileLinkDTO::getFilename).collect(Collectors.toSet());
        Assertions.assertThat(realFiles).isEqualTo(expected);
        return this;
    }

    public HomeworkAssert answerHasNoFiles(AnswerDTO answer) {
        AnswerDTO real = answers.get(answerId(answer));
        Assertions.assertThat(real.getFiles()).isEmpty();
        return this;
    }

    private HomeworkAssert assertFiles(List<FileLinkDTO> expected, List<FileLinkDTO> real) {
        assertEquals(expected.size(), real.size());

        expected = Util.sort(expected, FileLinkDTO::getFilename);
        real = Util.sort(real, FileLinkDTO::getFilename);

        for (int i = 0; i < expected.size(); i++) {
            assertEquals(expected.get(i).getFilename(), real.get(i).getFilename());
            assertEquals(expected.get(i).getSize(), real.get(i).getSize());
            assertEquals(expected.get(i).getUri(), real.get(i).getUri());
        }
        return this;
    }

    private HomeworkAssert assertHomeworkDetails(SimpleHomeworkDTO expected, SimpleHomeworkDTO real) {
        assertAll(
                () -> assertEquals(expected.getDescription(), real.getDescription()),
                () -> assertEquals(expected.getDeadline(), real.getDeadline()),
                () -> assertEquals(expected.getGroup(), real.getGroup()),
                () -> assertEquals(expected.getSubject(), real.getSubject()),
                () -> assertEquals(expected.getTitle(), real.getTitle()),
                () -> assertEquals(expected.getTeacherId(), real.getTeacherId()),
                () -> assertEquals(expected.getToEvaluate(), real.getToEvaluate()));
        return this;
    }

    private static boolean homeworksAreEqual(SimpleHomeworkDTO expected, SimpleHomeworkDTO real) {
        return expected.getToEvaluate().equals(real.getToEvaluate())
                && expected.getDeadline().equals(real.getDeadline())
                && expected.getDescription().equals(real.getDescription())
                && expected.getSubject().equals(real.getSubject())
                && expected.getGroup().equals(real.getGroup())
                && expected.getTeacherId().equals(real.getTeacherId())
                && expected.getTitle().equals(real.getTitle());
    }

    private String homeworkId(SimpleHomeworkDTO homework) {
        return homework.getTitle() + homework.getSubject() + homework.getGroup();
    }

    private String answerId(AnswerDTO answer) {
        return answer.getStudentId().get();
    }
}
