package com.sms.homeworkservice.answer.control;

import com.sms.api.grades.GradeDTO;
import com.sms.api.homework.AnswerDTO;
import com.sms.api.usermanagement.UserDTO;
import com.sms.homeworkservice.file.control.FileMapper;
import com.sms.model.grades.GradeJPA;
import com.sms.model.homework.AnswerJPA;

import java.util.Optional;
import java.util.stream.Collectors;

public class AnswerMapper {

    private AnswerMapper() {
    }

    public static AnswerDTO toDetailDTO(AnswerJPA jpa, UserDTO student) {
        return AnswerDTO.builder()
                .id(jpa.getId())
                .createdTime(jpa.getCreatedTime().toLocalDateTime())
                .lastUpdatedTime(jpa.getLastUpdatedTime().toLocalDateTime())
                .review(Optional.ofNullable(jpa.getReview()))
                .student(student)
                .grade(Optional.ofNullable(jpa.getGrade()).map(AnswerMapper::toDTO))
                .files(jpa.getFiles().stream()
                        .map(FileMapper::toDTO)
                        .collect(Collectors.toList()))
                .build();
    }

    // FIXME: copied from GradesMapper
    private static GradeDTO toDTO(GradeJPA grade) {
        return GradeDTO.builder()
                .id(Optional.ofNullable(grade.getId()))
                .description(Optional.ofNullable(grade.getDescription()))
                .grade(grade.getGrade())
                .subject(grade.getSubject())
                .weight(grade.getWeight())
                .studentId(grade.getStudentId())
                .teacherId(grade.getTeacherId())
                .createdTime(grade.getCreatedTime() == null
                        // FIXME: saving a grade does not return it's createdTime
                        ? grade.getLastUpdateTime().toLocalDateTime()
                        : grade.getCreatedTime().toLocalDateTime())
                .modifyTime(grade.getLastUpdateTime().toLocalDateTime())
                .isFinal(grade.getFinal())
                .build();
    }

    public static AnswerJPA toJPA(AnswerDTO answer) {
        AnswerJPA jpa = new AnswerJPA();
        answer.getId().ifPresent(jpa::setId);
        answer.getReview().ifPresent(jpa::setReview);
        answer.getStudent().map(UserDTO::getId).ifPresent(jpa::setStudentId);
        answer.getCreatedTime().ifPresent(jpa::setCreatedTime);
        answer.getLastUpdatedTime().ifPresent(jpa::setLastUpdatedTime);

        return jpa;
    }

    public static AnswerDTO toDTOSimple(AnswerJPA jpa) {
        return AnswerDTO.builder()
                .id(jpa.getId())
                .createdTime(jpa.getCreatedTime().toLocalDateTime())
                .lastUpdatedTime(jpa.getLastUpdatedTime().toLocalDateTime())
                .review(Optional.ofNullable(jpa.getReview()))
                .build();
    }
}
