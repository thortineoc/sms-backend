package com.sms.tests.grades;

import com.sms.api.grades.GradeDTO;
import com.sms.api.grades.GradesDTO;
import com.sms.api.grades.StudentGradesDTO;
import com.sms.tests.usermanagement.users.UserUtils;
import com.sms.api.usermanagement.UserDTO;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GradesAssert {

    private final Response response;
    private Map<String, UserDTO> studentsById;
    private List<StudentGradesDTO> studentGrades;
    private Map<String, GradesDTO> gradesBySubject;
    private final Map<String, List<BigDecimal>> responseGrades = new HashMap<>();

    public GradesAssert(Response response) {
        this.response = response;
        response.then().statusCode(HttpStatus.OK.value());
    }

    public void assertCanSeeStudents(UserDTO... students) {
        if (studentsById == null) {
            studentsById = getStudentsById();
        }

        for (UserDTO student : students) {
            Assertions.assertTrue(studentsById.containsKey(student.getId()));
            UserUtils.assertStudentsAreEqual(student, studentsById.get(student.getId()));
        }
    }

    public void assertCannotSeeStudents(UserDTO... students) {
        if (studentsById == null) {
            studentsById = getStudentsById();
        }

        for (UserDTO student : students) {
            Assertions.assertFalse(studentsById.containsKey(student.getId()));
        }
    }

    public void assertCanSeeStudentsGrades(String userId, double... grades) {
        if (studentGrades == null) {
            studentGrades = response.as(new TypeRef<List<StudentGradesDTO>>(){});
        }

        List<BigDecimal> decimalGrades = Arrays.stream(grades).mapToObj(BigDecimal::valueOf)
                .map(d -> d.setScale(2, RoundingMode.HALF_UP)).sorted()
                .collect(Collectors.toList());

        responseGrades.putIfAbsent(userId, getStudentGrades(userId));
        Assertions.assertEquals(decimalGrades, responseGrades.get(userId));
    }

    public void assertCannotSeeStudentsGrades(String userId) {
        if (studentGrades == null) {
            studentGrades = response.as(new TypeRef<List<StudentGradesDTO>>(){});
        }

        responseGrades.putIfAbsent(userId, getStudentGrades(userId));
        Assertions.assertTrue(responseGrades.get(userId).isEmpty());
    }

    public void assertHasNoGrades(String subject) {
        if (gradesBySubject == null) {
            gradesBySubject = response.as(new TypeRef<Map<String, GradesDTO>>() {});
        }
        Assertions.assertFalse(gradesBySubject.containsKey(subject));
    }

    public void assertHasGrades(String subject, double... grades) {
        if (gradesBySubject == null) {
            gradesBySubject = response.as(new TypeRef<Map<String, GradesDTO>>() {});
        }
        GradesDTO result = gradesBySubject.get(subject);

        List<BigDecimal> decimalGrades = Arrays.stream(grades).mapToObj(BigDecimal::valueOf)
                .map(d -> d.setScale(2, RoundingMode.HALF_UP)).sorted()
                .collect(Collectors.toList());
        List<BigDecimal> responseGrades = result.getGrades().stream().map(GradeDTO::getGrade)
                .map(d -> d.setScale(2, RoundingMode.HALF_UP)).sorted()
                .collect(Collectors.toList());
        Assertions.assertEquals(decimalGrades, responseGrades);
    }

    public void assertHasFinalGrade(String subject, GradeDTO grade) {
        if (gradesBySubject == null) {
            gradesBySubject = response.as(new TypeRef<Map<String, GradesDTO>>() {});
        }
        GradesDTO result = gradesBySubject.get(subject);

        Assertions.assertTrue(result.getFinalGrade().isPresent());
        assertEqualGrades(grade, result.getFinalGrade().get());
    }

    public void assertEqualGrades(GradeDTO expected, GradeDTO real) {
        Assertions.assertEquals(expected.getDescription(), real.getDescription());
        Assertions.assertEquals(expected.getGrade().setScale(2, RoundingMode.HALF_UP),
                real.getGrade().setScale(2, RoundingMode.HALF_UP));
        Assertions.assertEquals(expected.getSubject(), real.getSubject());
        Assertions.assertEquals(expected.getWeight(), real.getWeight());
        Assertions.assertEquals(expected.getStudentId(), real.getStudentId());
        Assertions.assertEquals(expected.getTeacherId(), real.getTeacherId());
        Assertions.assertEquals(expected.isFinal(), real.isFinal());
    }

    public List<Long> getStudentGradeIds(String userId) {
        if (gradesBySubject == null) {
            gradesBySubject = response.as(new TypeRef<Map<String, GradesDTO>>() {});
        }
        return gradesBySubject.values().stream()
                .map(GradesDTO::getGrades)
                .flatMap(Collection::stream)
                .map(GradeDTO::getId)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    private Map<String, UserDTO> getStudentsById() {
        List<StudentGradesDTO> result = response.as(new TypeRef<List<StudentGradesDTO>>(){});
        return result.stream()
                .map(StudentGradesDTO::getStudent)
                .collect(Collectors.toMap(UserDTO::getId, Function.identity()));
    }

    private List<BigDecimal> getStudentGrades(String userId) {
        return getStudentGradeDTOs(userId).stream()
                .map(GradeDTO::getGrade)
                .map(d -> d.setScale(2, RoundingMode.HALF_UP)).sorted()
                .collect(Collectors.toList());
    }

    private List<GradeDTO> getStudentGradeDTOs(String userId) {
        return studentGrades.stream()
                .filter(sg -> sg.getStudent().getId().equals(userId))
                .map(StudentGradesDTO::getGrades)
                .map(GradesDTO::getGrades)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }
}
