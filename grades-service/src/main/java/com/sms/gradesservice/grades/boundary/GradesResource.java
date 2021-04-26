package com.sms.gradesservice.grades.boundary;


import com.sms.context.AuthRole;
import com.sms.grades.GradeDTO;
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

    @PostMapping("/student")
    @AuthRole(UserDTO.Role.STUDENT)
    public ResponseEntity<Map<String, List<GradeDTO>>> getStudentGrades() {
        Map<String, List<GradeDTO>> grades = gradesService.getStudentGrades();
        return ResponseEntity.ok(grades);
    }

    @PostMapping("/teacher/{subject}")
    @AuthRole(UserDTO.Role.TEACHER)
    public ResponseEntity<Map<String, List<GradeDTO>>> getTeacherGrades(@PathVariable("subject") String subject,
                                                                        @RequestBody List<String> studentIds) {
        Map<String, List<GradeDTO>> grades = gradesService.getTeacherGrades(subject, studentIds);
        return ResponseEntity.ok(grades);
    }
}
