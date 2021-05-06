package com.sms.gradesservice.grades.control;

import com.sms.grades.GradeDTO;
import com.sms.gradesservice.grades.control.repository.GradeJPA;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;

public class GradesMapper {

    private GradesMapper() {
    }

    public static GradeDTO toDTO(GradeJPA grade) {
        return GradeDTO.builder()
                .id(Optional.ofNullable(grade.getId()))
                .description(Optional.ofNullable(grade.getDescription()))
                .grade(grade.getGrade())
                .subject(grade.getSubject())
                .weight(grade.getWeight())
                .studentId(grade.getStudentId())
                .teacherId(grade.getTeacherId())
                .createdTime(grade.getCreatedTime() == null
                        ? grade.getLastUpdateTime().toLocalDateTime()
                        : grade.getCreatedTime().toLocalDateTime())
                .modifyTime(grade.getLastUpdateTime().toLocalDateTime())
                .isFinal(grade.getFinal())
                .build();
    }

    public static GradeJPA toJPA(GradeDTO gradeDTO) {
        GradeJPA grade = new GradeJPA();
        grade.setGrade(gradeDTO.getGrade());
        grade.setStudentId(gradeDTO.getStudentId());
        grade.setSubject(gradeDTO.getSubject());
        grade.setWeight(gradeDTO.getWeight());
        grade.setIsFinal(gradeDTO.isFinal());
        grade.setLastUpdateTime(Timestamp.from(Instant.now()));
        gradeDTO.getTeacherId().ifPresent(grade::setTeacherId);
        gradeDTO.getId().ifPresent(grade::setId);
        gradeDTO.getDescription().ifPresent(grade::setDescription);
        return grade;
    }
}
