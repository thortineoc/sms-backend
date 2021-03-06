package com.sms.gradesservice.grades.boundary;


import com.sms.context.AuthRole;
import com.sms.api.grades.GradeDTO;
import com.sms.api.grades.GradesDTO;
import com.sms.api.grades.StudentGradesDTO;
import com.sms.gradesservice.grades.control.GradesService;
import com.sms.api.usermanagement.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/grades")
@Scope("request")
public class GradesResource {

    @Autowired
    GradesService gradesService;

    @GetMapping("/{id}")
    @AuthRole(UserDTO.Role.ADMIN)
    public ResponseEntity<GradeDTO> getGradeById(@PathVariable("id") Long id) {
        Optional<GradeDTO> grade = gradesService.getGrade(id);
        return grade.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.noContent().build());
    }

    @GetMapping("/student")
    @AuthRole({UserDTO.Role.STUDENT, UserDTO.Role.PARENT})
    public ResponseEntity<Map<String, GradesDTO>> getStudentGrades() {
        Map<String, GradesDTO> grades = gradesService.getStudentGrades();
        if (grades.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(grades);
        }
    }

    @GetMapping("/group/{groupName}/subject/{subjectName}")
    @AuthRole(UserDTO.Role.TEACHER)
    public ResponseEntity<List<StudentGradesDTO>> getTeacherGrades(@PathVariable("groupName") String groupName,
                                                                   @PathVariable("subjectName") String subjectName) {
        List<StudentGradesDTO> grades = gradesService.getTeacherGrades(groupName, subjectName);
        if (grades.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(grades);
        }
    }

    @PutMapping
    @AuthRole(UserDTO.Role.TEACHER)
    public ResponseEntity<GradeDTO> updateGrade(@RequestBody GradeDTO grade) {
        GradeDTO updatedGrade = gradesService.updateGrade(grade);
        return ResponseEntity.ok(updatedGrade);
    }

    @DeleteMapping("/{id}")
    @AuthRole(UserDTO.Role.TEACHER)
    public ResponseEntity<Object> deleteGrade(@PathVariable("id") Long id) {
        gradesService.deleteGrade(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/subject/{subject}")
    @AuthRole(UserDTO.Role.ADMIN)
    public ResponseEntity<Object> deleteGradeBySubject(@PathVariable("subject") String subject) {
        gradesService.deleteBySubject(subject);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/user/{id}")
    @AuthRole(UserDTO.Role.ADMIN)
    public ResponseEntity<Object> deleteAllGrades(@PathVariable("id") String id) {
        gradesService.deleteAllGrades(id);
        return ResponseEntity.noContent().build();
    }
}
