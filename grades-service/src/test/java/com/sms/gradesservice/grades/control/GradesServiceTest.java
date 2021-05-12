package com.sms.gradesservice.grades.control;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.sms.api.grades.GradeDTO;
import com.sms.api.grades.GradesDTO;
import com.sms.api.grades.StudentGradesDTO;
import com.sms.api.usermanagement.CustomAttributesDTO;
import com.sms.api.usermanagement.UserDTO;
import com.sms.model.grades.GradeJPA;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

class GradesServiceTest {

    private static final String FIRST_STUDENT = "1";
    private static final String SECOND_STUDENT = "2";
    private static final String MATHS = "Maths";
    private static final String PHYSICS = "Physics";

    private final GradesService gradesService = new GradesService();

    @Test
    void shouldExtractFinalGrade() {
        // GIVEN
        Map<String, List<GradeDTO>> grades = Lists.newArrayList(
                getGrade(MATHS, FIRST_STUDENT, false),
                getGrade(MATHS, FIRST_STUDENT, false),
                getGrade(MATHS, FIRST_STUDENT, false),
                getGrade(PHYSICS, FIRST_STUDENT, true)
        ).stream().collect(Collectors.groupingBy(GradeDTO::getStudentId));

        // WHEN
        Map<String, GradesDTO> result = gradesService.extractFinalGrades(grades);
        GradesDTO calculatedGrades = result.get(FIRST_STUDENT);

        // THEN
        Assertions.assertEquals(3, calculatedGrades.getGrades().size());
        Assertions.assertTrue(calculatedGrades.getFinalGrade().isPresent());
        Assertions.assertTrue(calculatedGrades.getFinalGrade().get().isFinal());
        Assertions.assertEquals(PHYSICS, calculatedGrades.getFinalGrade().get().getSubject());
    }

    @Test
    void shouldNotExtractFinalGrade() {
        // GIVEN
        Map<String, List<GradeDTO>> grades = Lists.newArrayList(
                getGrade(MATHS, FIRST_STUDENT, false),
                getGrade(MATHS, FIRST_STUDENT, false),
                getGrade(MATHS, FIRST_STUDENT, false),
                getGrade(PHYSICS, FIRST_STUDENT, false)
        ).stream().collect(Collectors.groupingBy(GradeDTO::getStudentId));

        // WHEN
        Map<String, GradesDTO> result = gradesService.extractFinalGrades(grades);
        GradesDTO calculatedGrades = result.get(FIRST_STUDENT);

        // THEN
        Assertions.assertEquals(4, calculatedGrades.getGrades().size());
        Assertions.assertFalse(calculatedGrades.getFinalGrade().isPresent());
    }

    @Test
    void shouldGroupBySubject() {
        // GIVEN
        List<GradeJPA> grades = Lists.newArrayList(
                getGradeJPA(MATHS, FIRST_STUDENT, false),
                getGradeJPA(PHYSICS, FIRST_STUDENT, false),
                getGradeJPA(MATHS, FIRST_STUDENT, false),
                getGradeJPA(PHYSICS, FIRST_STUDENT, false)
        );

        // WHEN
        Map<String, List<GradeDTO>> result = gradesService.groupGrades(GradeDTO::getSubject, grades);

        // THEN
        Assertions.assertEquals(2, result.get(MATHS).size());
        Assertions.assertEquals(2, result.get(PHYSICS).size());
    }

    @Test
    void shouldMapStudentsToGrades() {
        // GIVEN
        Map<String, GradesDTO> grades = gradesService.extractFinalGrades(Lists.newArrayList(
                getGrade(MATHS, SECOND_STUDENT, false),
                getGrade(MATHS, FIRST_STUDENT, false),
                getGrade(MATHS, SECOND_STUDENT, false),
                getGrade(PHYSICS, FIRST_STUDENT, false)
        ).stream().collect(Collectors.groupingBy(GradeDTO::getStudentId)));

        Map<String, UserDTO> students = ImmutableMap.of(
                FIRST_STUDENT, getUserDTO(FIRST_STUDENT),
                SECOND_STUDENT, getUserDTO(SECOND_STUDENT));

        // WHEN
        List<StudentGradesDTO> mappedGrades = gradesService.mapStudentsToGrades(grades, students);

        // THEN
        Assertions.assertEquals(2, mappedGrades.size());
        Assertions.assertTrue(mappedGrades.stream().anyMatch(gradeMapping -> FIRST_STUDENT.equals(gradeMapping.getStudent().getId())));
        Assertions.assertTrue(mappedGrades.stream().anyMatch(gradeMapping -> SECOND_STUDENT.equals(gradeMapping.getStudent().getId())));
    }

    private UserDTO getUserDTO(String id) {
        return UserDTO.builder()
                .role(UserDTO.Role.STUDENT)
                .pesel(UUID.randomUUID().toString())
                .userName(UUID.randomUUID().toString())
                .id(id)
                .email(UUID.randomUUID().toString())
                .firstName(UUID.randomUUID().toString())
                .lastName(UUID.randomUUID().toString())
                .customAttributes(CustomAttributesDTO.builder().build())
                .build();
    }

    private GradeJPA getGradeJPA(String subject, String studentId, boolean isFinal) {
        GradeJPA grade = new GradeJPA();
        grade.setLastUpdateTime(Timestamp.from(Instant.now()));
        grade.setCreatedTime(Timestamp.from(Instant.now()));
        grade.setGrade(BigDecimal.ONE);
        grade.setStudentId(studentId);
        grade.setTeacherId(UUID.randomUUID().toString());
        grade.setSubject(subject);
        grade.setWeight(1);
        grade.setIsFinal(isFinal);
        return grade;
    }

    private GradeDTO getGrade(String subject, String studentId, boolean isFinal) {
        return GradeDTO.builder()
                .subject(subject)
                .description(UUID.randomUUID().toString())
                .createdTime(LocalDateTime.now())
                .grade(BigDecimal.ONE)
                .isFinal(isFinal)
                .studentId(studentId)
                .weight(1)
                .build();
    }
}
