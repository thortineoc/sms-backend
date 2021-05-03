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

    @DeleteMapping("/user/{id}")
    @AuthRole(UserDTO.Role.ADMIN)
    public ResponseEntity<Object> deleteAllGrades( @PathVariable("id") String id) {
        gradesService.deleteAllGrades(id);
        return ResponseEntity.noContent().build();
    }


}
