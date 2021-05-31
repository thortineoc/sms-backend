package com.sms.tests.homework;

import com.sms.api.grades.GradeDTO;
import com.sms.api.homework.*;
import com.sms.api.usermanagement.UserDTO;
import com.sms.clients.Environment;
import com.sms.clients.WebClient;
import io.restassured.builder.MultiPartSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.MultiPartSpecification;
import io.restassured.specification.RequestSpecification;

import javax.ws.rs.core.MediaType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class HomeworkClient {

    protected final WebClient client;
    protected final UserDTO user;

    public HomeworkClient(UserDTO user) {
        this.user = user;
        String password = user.getFirstName().substring(0, Math.min(4, user.getFirstName().length()))
                + user.getLastName().substring(0, Math.min(user.getLastName().length(), 4));
        this.client = new WebClient(user.getUserName(), password);
    }

    public UserDTO getUser() {
        return user;
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

    public static AnswerDTO getAnswerDTO(String review, String studentId, List<FileLinkDTO> files, GradeDTO grade) {
        return AnswerDTO.builder()
                .review(Optional.ofNullable(review))
                .studentId(studentId)
                .files(files)
                .grade(Optional.ofNullable(grade))
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
        return getRequest().body(answer).put("/answer");
    }

    public Response queryHomeworkDetails(Long id) {
        return getRequest().get("/homework/" + id);
    }

    public Response uploadFile(Long relationId, FileLinkDTO.Type type, MultiPartSpecification file) {
        return getMultipartRequest().multiPart(file)
                .post("/files/upload/" + relationId + "/" + type.toString());
    }

    public Response downloadFile(Long fileId) {
        return getOctetStreamRequest().get("/files/id/" + fileId);
    }

    public Response deleteFile(Long fileId) {
        return getRequest().delete("/files/" + fileId);
    }

    public static MultiPartSpecification getFile(String filename, byte[] content) {
        return new MultiPartSpecBuilder(content)
                .fileName(filename)
                .mimeType("text/plain")
                .controlName("file")
                .build();
    }

    public RequestSpecification getRequest() {
        return client.request(Environment.HOMEWORK)
                .contentType(MediaType.APPLICATION_JSON)
                .log().all();
    }

    public RequestSpecification getOctetStreamRequest() {
        return client.request(Environment.HOMEWORK)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .log().all();
    }

    public RequestSpecification getMultipartRequest() {
        return client.request(Environment.HOMEWORK)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .log().all();
    }
}
