package com.sms.tests.grades;

import com.sms.clients.Environment;
import com.sms.clients.WebClient;
import com.sms.grades.GradeDTO;
import com.sms.grades.GradesDTO;
import com.sms.usermanagement.UserDTO;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.HttpStatus;

import javax.ws.rs.core.MediaType;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GradeUtils {

    public static final WebClient ADMIN = new WebClient("smsadmin", "smsadmin");
    public static final WebClient TEACHER = new WebClient("teacher", "teacher");
    public static final WebClient STUDENT = new WebClient("student", "student");

    public static Response teacherGetGrades(WebClient client, String group, String subject) {
        return client.request(Environment.GRADES)
                .contentType(MediaType.APPLICATION_JSON)
                .log().all()
                .get("/grades/group/" + group + "/subject/" + subject);
    }

    public static Response studentGetGrades(WebClient client) {
        return client.request(Environment.GRADES)
                .contentType(MediaType.APPLICATION_JSON)
                .log().all()
                .get("/grades/student");
    }

    public static Response teacherGetGrades(String group, String subject) {
        return teacherGetGrades(TEACHER, group, subject);
    }

    public static Response studentGetGrades() {
        return studentGetGrades(STUDENT);
    }

    public static Response saveGrade(WebClient client, GradeDTO grade) {
        return client.request(Environment.GRADES)
                .contentType(MediaType.APPLICATION_JSON)
                .log().all()
                .body(grade)
                .put("/grades");
    }

    public static Response saveGrade(GradeDTO grade) {
        return saveGrade(TEACHER, grade);
    }

    public static List<Response> saveGrades(UserDTO student, String subject, double... grades) {
        return Arrays.stream(grades).mapToObj(g -> getGradeDTO(student.getId(), subject, g, false))
                .map(GradeUtils::saveGrade).collect(Collectors.toList());
    }

    public static Response saveFinalGrade(UserDTO student, String subject, double grade) {
        return saveGrade(getGradeDTO(student.getId(), subject, grade, true));
    }

    public static Response deleteGrade(WebClient client, Long id) {
        return client.request(Environment.GRADES)
                .contentType(MediaType.APPLICATION_JSON)
                .log().all()
                .delete("/grades/" + id);
    }

    public static Response deleteGrade(Long id) {
        return deleteGrade(TEACHER, id);
    }

    public static Response deleteUserGrades(String userId) {
        return ADMIN.request(Environment.GRADES)
                .contentType(MediaType.APPLICATION_JSON)
                .log().all()
                .delete("/grades/user/" + userId);
    }

    public static GradeDTO getGradeDTO(String studentId, String subject, double grade, boolean isFinal) {
        return GradeDTO.builder()
                .studentId(studentId)
                .subject(subject)
                .grade(BigDecimal.valueOf(grade))
                .isFinal(isFinal)
                .build();
    }

    public static void assertHasGrades(Response response, String subject, double... grades) {
        response.then().statusCode(HttpStatus.OK.value());
        GradesDTO result = response.as(new TypeRef<Map<String, GradesDTO>>(){}).get(subject);

        List<BigDecimal> decimalGrades = Arrays.stream(grades).mapToObj(BigDecimal::valueOf)
                .map(d -> d.setScale(2, RoundingMode.HALF_UP)).sorted()
                .collect(Collectors.toList());
        List<BigDecimal> responseGrades = result.getGrades().stream().map(GradeDTO::getGrade)
                .map(d -> d.setScale(2, RoundingMode.HALF_UP)).sorted()
                .collect(Collectors.toList());
        Assertions.assertEquals(decimalGrades, responseGrades);
    }

    public static void assertHasNoGrades(Response response, String subject) {
        response.then().statusCode(HttpStatus.OK.value());
        Assertions.assertNull(response.as(new TypeRef<Map<String, GradesDTO>>(){}).get(subject));
    }
}
