package com.sms.gradesservice.grades.boundary;


import com.sms.context.AuthRole;
import com.sms.grades.GradeDTO;
import com.sms.grades.GradesDTO;
import com.sms.grades.StudentGradesDTO;
import com.sms.gradesservice.grades.control.GradesService;
import com.sms.usermanagement.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/grades")
@Scope("request")
public class GradesResource {

    @Autowired
    GradesService gradesService;

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

    @PostMapping("/teacher/{subject}")
    @AuthRole(UserDTO.Role.TEACHER)
    public ResponseEntity<List<StudentGradesDTO>> getTeacherGrades(@PathVariable("subject") String subject,
                                                                   @RequestBody List<String> studentIds) {
        List<StudentGradesDTO> grades = gradesService.getTeacherGrades(subject, studentIds);
        if (grades.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(grades);
        }
    }

    @PutMapping
    @AuthRole(UserDTO.Role.TEACHER)
    public ResponseEntity<Object> updateGrade(@RequestBody GradeDTO grade) {
        gradesService.updateGrade(grade);
        return ResponseEntity.ok().build();
    }
}
