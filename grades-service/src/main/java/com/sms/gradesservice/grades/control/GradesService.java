package com.sms.gradesservice.grades.control;

import com.sms.context.UserContext;
import com.sms.grades.GradeDTO;
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

@Component
@Scope("request")
public class GradesService {

    @Autowired
    UserContext userContext;

    @Autowired
    GradesRepository gradesRepository;

    @Autowired
    UserManagementClient userManagementClient;

    public Map<String, List<GradeDTO>> getStudentGrades() {
        String studentId = userContext.getUserId();
        try {
            return groupBySubject(gradesRepository.findAllByStudentId(studentId));
        } catch (EntityNotFoundException e) {
            return Collections.emptyMap();
        }
    }

    public List<StudentGradesDTO> getTeacherGrades(String subject, List<String> studentIds) {
        try {
            Map<String, List<GradeDTO>> grades = groupByStudentId(gradesRepository.findAllBySubjectAndStudentIdIn(subject, studentIds));
            Map<String, UserDTO> studentsByIds = getStudentsByIds();
            return mapStudentsToGrades(grades, studentsByIds);
        } catch (EntityNotFoundException e) {
            return Collections.emptyList();
        }
    }

    public void updateGrade(GradeDTO gradeDTO) {
        GradeJPA grade = GradesMapper.toJPA(gradeDTO);
        validateGrade(grade);

        try {
            gradesRepository.save(grade);
        } catch (ConstraintViolationException e) {
            throw new IllegalArgumentException("Saving grade: " + gradeDTO.getId() + " violated database constraints: " + e.getConstraintName());
        } catch (EntityNotFoundException e) {
            throw new IllegalStateException("Grade with ID: " + gradeDTO.getId() + " does not exist, can't update: " + e.getMessage());
        }
    }

    private List<StudentGradesDTO> mapStudentsToGrades(Map<String, List<GradeDTO>> grades, Map<String, UserDTO> students) {
        return grades.keySet().stream()
                .map(id -> StudentGradesDTO.builder()
                        .grades(grades.get(id))
                        .student(Optional.ofNullable(students.get(id))
                                .orElseThrow(() -> new IllegalStateException("Grades assigned to non existing user: " + id + " found")))
                        .build())
                .collect(Collectors.toList());
    }

    private Map<String, List<GradeDTO>> groupByStudentId(List<GradeJPA> grades) {
        return grades.stream().map(GradesMapper::toDTO).collect(Collectors.groupingBy(GradeDTO::getStudentId));
    }

    private Map<String, List<GradeDTO>> groupBySubject(List<GradeJPA> grades) {
        return grades.stream().map(GradesMapper::toDTO).collect(Collectors.groupingBy(GradeDTO::getSubject));
    }

    private Map<String, UserDTO> getStudentsByIds() {
        return userManagementClient.getUsers(UsersFiltersDTO.builder()
                .role(UserDTO.Role.STUDENT.toString())
                .build()).stream()
                .collect(Collectors.toMap(UserDTO::getId, Function.identity()));
    }

    private void validateGrade(GradeJPA grade) {
        if (grade.getGrade() < 1) {
            throw new IllegalArgumentException("Grade cannot be 0 or negative");
        }
        if (grade.getWeight() < 1) {
            throw new IllegalArgumentException("Grade weight cannot be 0 or negative");
        }
        Optional<UserDTO> teacherUser = userManagementClient.getUser(grade.getTeacherId());
        if (!teacherUser.isPresent()) {
            throw new IllegalArgumentException("Teacher user: " + grade.getTeacherId() + " does not exist");
        }
        Optional<UserDTO> studentUser = userManagementClient.getUser(grade.getStudentId());
        if (!studentUser.isPresent()) {
            throw new IllegalArgumentException("Student user: " + grade.getStudentId() + " does not exist");
        }
    }
}
