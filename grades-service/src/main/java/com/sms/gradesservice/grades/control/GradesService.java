package com.sms.gradesservice.grades.control;

import com.sms.common.Util;
import com.sms.context.UserContext;
import com.sms.grades.GradeDTO;
import com.sms.grades.GradesDTO;
import com.sms.grades.StudentGradesDTO;
import com.sms.gradesservice.clients.UserManagementClient;
import com.sms.gradesservice.grades.control.repository.GradeJPA;
import com.sms.gradesservice.grades.control.repository.GradesRepository;
import com.sms.usermanagement.UserDTO;
import com.sms.usermanagement.UsersFiltersDTO;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.EntityNotFoundException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.sms.common.Util.*;

@Component
@Scope("request")
public class GradesService {

    @Autowired
    UserContext userContext;

    @Autowired
    GradesRepository gradesRepository;

    @Autowired
    UserManagementClient userManagementClient;

    public Map<String, GradesDTO> getStudentGrades() {
        String studentId = userContext.getUserId();
        try {
            return extractFinalGrades(groupGrades(GradeDTO::getSubject, gradesRepository.findAllByStudentId(studentId)));
        } catch (EntityNotFoundException e) {
            return Collections.emptyMap();
        }
    }

    public List<StudentGradesDTO> getTeacherGrades(String group, String subject) {
        try {
            Map<String, UserDTO> studentsByIds = getStudentsByIds(group);
            Map<String, List<GradeDTO>> grades = groupGrades(GradeDTO::getStudentId,
                    gradesRepository.findAllBySubjectAndStudentIdIn(subject, studentsByIds.keySet()));
            return mapStudentsToGrades(extractFinalGrades(grades), studentsByIds);
        } catch (EntityNotFoundException e) {
            return Collections.emptyList();
        }
    }

    public GradeDTO updateGrade(GradeDTO gradeDTO) {
        GradeJPA grade = GradesMapper.toJPA(gradeDTO);
        grade.setTeacherId(userContext.getUserId());
        validateGrade(grade);

        try {
            GradeJPA updatedGrade = gradesRepository.save(grade);
            return GradesMapper.toDTO(updatedGrade);
        } catch (ConstraintViolationException e) {
            throw new IllegalArgumentException("Saving grade: " + gradeDTO.getId() + " violated database constraints: " + e.getConstraintName());
        } catch (EntityNotFoundException e) {
            throw new IllegalStateException("Grade with ID: " + gradeDTO.getId() + " does not exist, can't update: " + e.getMessage());
        }
    }

    public void deleteGrade(Long id) {
        try {
            gradesRepository.deleteById(id);
        } catch (ConstraintViolationException e) {
            throw new IllegalArgumentException("Deleting grade: " + id + " violated database constraints: " + e.getConstraintName());
        } catch (EntityNotFoundException e) {
            throw new IllegalStateException("Grade with ID: " + id + " does not exist, can't delete: ");
        }
    }

    public void deleteAllGrades(String id){
        List<GradeJPA> studentGrades = gradesRepository.findAllByStudentId(id);
        for( GradeJPA grade : studentGrades ) {
            try {
                gradesRepository.deleteById(grade.getId());
            } catch (ConstraintViolationException e) {
                throw new IllegalArgumentException("Deleting grade: " + grade.getId() + " violated database constraints: " + e.getConstraintName());
            } catch (EntityNotFoundException e) {
                throw new IllegalStateException("Grade with ID: " + grade.getId() + " does not exist, can't delete: ");
            }
        }
    }

    List<StudentGradesDTO> mapStudentsToGrades(Map<String, GradesDTO> grades, Map<String, UserDTO> students) {
        return grades.keySet().stream()
                .map(id -> StudentGradesDTO.builder()
                        .grades(grades.get(id))
                        .student(getOrThrow(students, id,
                                () -> new IllegalStateException("Grades assigned to non existing user: " + id + " found")))
                        .build())
                .collect(Collectors.toList());
    }

    Map<String, List<GradeDTO>> groupGrades(Function<GradeDTO, String> classifier, List<GradeJPA> grades) {
        return grades.stream().map(GradesMapper::toDTO).collect(Collectors.groupingBy(classifier));
    }

    Map<String, GradesDTO> extractFinalGrades(Map<String, List<GradeDTO>> grades) {
        return grades.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, this::extractFinalGrade));
    }

    private GradesDTO extractFinalGrade(Map.Entry<String, List<GradeDTO>> grades) {
        Map<Boolean, List<GradeDTO>> splitGrades = grades.getValue().stream()
                .collect(Collectors.groupingBy(GradeDTO::isFinal));

        return GradesDTO.builder()
                .grades(getOrEmpty(splitGrades, Boolean.FALSE))
                .finalGrade(getOpt(splitGrades, Boolean.TRUE).flatMap(Util::getFirst))
                .build();
    }

    private Map<String, UserDTO> getStudentsByIds(String group) {
        return userManagementClient.getUsers(UsersFiltersDTO.builder()
                .role(UserDTO.Role.STUDENT.toString())
                .group(group)
                .build()).stream()
                .collect(Collectors.toMap(UserDTO::getId, Function.identity()));
    }

    private void validateGrade(GradeJPA grade) {
        if (grade.getWeight() < 1) {
            throw new IllegalArgumentException("Grade weight cannot be 0 or negative");
        }
        Optional<UserDTO> studentUser = userManagementClient.getUser(grade.getStudentId());
        if (!studentUser.isPresent()) {
            throw new IllegalArgumentException("Student user: " + grade.getStudentId() + " does not exist");
        }
    }
}
