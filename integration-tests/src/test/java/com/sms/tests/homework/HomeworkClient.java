package com.sms.tests.homework;

import com.sms.api.grades.GradeDTO;
import com.sms.api.homework.*;
import com.sms.api.usermanagement.UserDTO;
import com.sms.clients.Environment;
import com.sms.clients.WebClient;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import javax.ws.rs.core.MediaType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class HomeworkClient {

    protected final WebClient client;
    protected final UserDTO user;

    public HomeworkClient(UserDTO user) {
        this.user = user;
        String password = user.getFirstName().substring(0, 4) + user.getLastName().substring(0, 4);
        this.client = new WebClient(user.getUserName(), password);
    }

    public UserDTO getUser() {
        return user;
    }

    public static HomeworkDTO getHomeworkDTO(SimpleHomeworkDTO simpleHomework, List<AnswerWithStudentDTO> answers, List<FileLinkDTO> files) {
        return HomeworkDTO.builder().from(simpleHomework)
                .answers(answers)
                .files(files)
                .build();
    }

    public static SimpleHomeworkDTO getSimpleHomeworkDTO(String title, String group, String subject, boolean toEvaluate, LocalDateTime deadline) {
        return SimpleHomeworkDTO.builder()
                .deadline(deadline)
                .title(title)
                .group(group)
                .subject(subject)
                .toEvaluate(toEvaluate)
                .build();
    }

    public static SimpleHomeworkDTO getSimpleHomeworkDTO(Long id, String title, String group, String subject, boolean toEvaluate, LocalDateTime deadline) {
        return SimpleHomeworkDTO.builder()
                .from(getSimpleHomeworkDTO(title, group, subject, toEvaluate, deadline))
                .id(id)
                .build();
    }

    public static AnswerWithStudentDTO getAnswerWithStudentDTO(AnswerDTO answer, UserDTO student) {
        return AnswerWithStudentDTO.builder()
                .answer(Optional.ofNullable(answer))
                .student(student)
                .build();
    }

    public static AnswerDTO getAnswerDTO(String review, String studentId, List<FileLinkDTO> files, GradeDTO grade) {
        return AnswerDTO.builder()
                .review(Optional.ofNullable(review))
                .studentId(studentId)
                .files(files)
                .grade(Optional.ofNullable(grade))
                .build();
    }

    public static AnswerDTO getAnswerDTO(Long id, String review, String studentId, List<FileLinkDTO> files, GradeDTO grade) {
        return AnswerDTO.builder()
                .from(getAnswerDTO(review, studentId, files, grade))
                .id(id)
                .build();
    }

    public static FileLinkDTO getFileLinkDTO(String filename) {
        return FileLinkDTO.builder()
                .filename(filename)
                .uri("http://localhost:8080/homework-service/files/FAKE_ID")
                .size((long) filename.length())
                .build();
    }

    public static FileLinkDTO getFileLinkDTO(Long id, String filename) {
        return FileLinkDTO.builder()
                .from(getFileLinkDTO(filename))
                .id(id)
                .build();
    }

    public Response updateAnswer(AnswerDTO answer) {
        return getRequest().put("/answer", answer);
    }

    public Response queryHomeworkDetails(Long id) {
        return getRequest().get("/homework/" + id);
    }

    public Response uploadFile(Long relationId, FileLinkDTO.Type type, MultipartFile file) {
        return getRequest().put("/files/upload/" + relationId + "/" + type.toString(), file);
    }

    public Response downloadFile(Long fileId) {
        return getRequest().get("/files/" + fileId);
    }

    public Response deleteFile(Long fileId) {
        return getRequest().delete("/files/" + fileId);
    }

    public static MultipartFile getFile(String filename, byte[] content) {
        return new MockMultipartFile(filename, content);
    }

    public RequestSpecification getRequest() {
        return client.request(Environment.HOMEWORK)
                .contentType(MediaType.APPLICATION_JSON)
                .log().all();
    }
}
