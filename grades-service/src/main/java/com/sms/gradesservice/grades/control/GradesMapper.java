package com.sms.gradesservice.grades.control;

import com.sms.grades.GradeDTO;
import com.sms.gradesservice.grades.control.repository.GradeJPA;

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
                .studentId(grade.getStudentId())
                .teacherId(grade.getTeacherId())
                .build();
    }

    public static GradeJPA toJPA(GradeDTO gradeDTO) {
        GradeJPA grade = new GradeJPA();
        grade.setGrade(gradeDTO.getGrade());
        grade.setStudentId(gradeDTO.getStudentId());
        grade.setTeacherId(gradeDTO.getTeacherId());
        grade.setWeight(gradeDTO.getWeight());
        gradeDTO.getId().ifPresent(grade::setId);
        gradeDTO.getDescription().ifPresent(grade::setDescription);
        return grade;
    }
}
