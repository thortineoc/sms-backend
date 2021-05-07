package com.sms.tests.grades;

import com.sms.clients.Environment;
import com.sms.clients.WebClient;
import com.sms.grades.GradeDTO;
import com.sms.grades.GradesDTO;
import com.sms.grades.StudentGradesDTO;
import com.sms.usermanagement.UserDTO;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;

import javax.ws.rs.core.MediaType;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class GradeUtils {

    public static final WebClient ADMIN = new WebClient("smsadmin", "smsadmin");
    public static WebClient TEACHER;

    public static void useTeacher(WebClient client) {
        TEACHER = client;
    }

    public static Response teacherGetGrades(String group, String subject) {
        return TEACHER.request(Environment.GRADES)
                .contentType(MediaType.APPLICATION_JSON)
                .log().all()
                .get("/grades/group/" + group + "/subject/" + subject);
    }

    public static List<GradeDTO> getTeacherGrades(Response response, String studentId) {
        response.then().statusCode(HttpStatus.OK.value());
        List<StudentGradesDTO> studentGrades = response.as(new TypeRef<List<StudentGradesDTO>>(){});
        return studentGrades.stream()
                .filter(sg -> sg.getStudent().getId().equals(studentId))
                .map(StudentGradesDTO::getGrades)
                .map(GradesDTO::getGrades)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    public static Response studentGetGrades(WebClient client) {
        return client.request(Environment.GRADES)
                .contentType(MediaType.APPLICATION_JSON)
                .log().all()
                .get("/grades/student");
    }

    public static Response getGrade(Long id) {
        return ADMIN.request(Environment.GRADES)
                .contentType(MediaType.APPLICATION_JSON)
                .log().all()
                .get("/grades/" + id);
    }

    public static Response saveGrade(GradeDTO grade) {
        return TEACHER.request(Environment.GRADES)
                .contentType(MediaType.APPLICATION_JSON)
                .log().all()
                .body(grade)
                .put("/grades");
    }

    public static List<Response> saveGrades(UserDTO student, String subject, double... grades) {
        return Arrays.stream(grades).mapToObj(g -> getGradeDTO(student.getId(), subject, g, false))
                .map(GradeUtils::saveGrade).collect(Collectors.toList());
    }

    public static Response saveFinalGrade(UserDTO student, String subject, double grade) {
        return saveGrade(getGradeDTO(student.getId(), subject, grade, true));
    }

    public static Response deleteGrade(Long id) {
        return TEACHER.request(Environment.GRADES)
                .contentType(MediaType.APPLICATION_JSON)
                .log().all()
                .delete("/grades/" + id);
    }

    public static Response deleteUserGrades(String userId) {
        return ADMIN.request(Environment.GRADES)
                .contentType(MediaType.APPLICATION_JSON)
                .log().all()
                .delete("/grades/user/" + userId);
    }

    public static GradeDTO getGradeDTO(GradeDTO from, double grade) {
        return GradeDTO.builder().from(from)
                .grade(BigDecimal.valueOf(grade))
                .build();
    }

    public static GradeDTO getGradeDTO(String studentId, String subject, double grade, boolean isFinal) {
        return getGradeDTO(studentId, subject, grade, isFinal, null);
    }

    public static GradeDTO getGradeDTO(String studentId, String subject, double grade, boolean isFinal, String description) {
        return GradeDTO.builder()
                .studentId(studentId)
                .subject(subject)
                .description(Optional.ofNullable(description))
                .grade(BigDecimal.valueOf(grade))
                .isFinal(isFinal)
                .build();
    }
}
