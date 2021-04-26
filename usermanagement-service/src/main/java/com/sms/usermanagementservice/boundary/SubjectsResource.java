package com.sms.usermanagementservice.boundary;

import com.sms.context.AuthRole;
import com.sms.usermanagement.UserDTO;
import com.sms.usermanagementservice.control.subjects.SubjectsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/subjects")
@Scope("request")
public class SubjectsResource {

    @Autowired
    private SubjectsService subjectsService;

    @GetMapping
    public ResponseEntity<List<String>> getAllSubjects() {
        List<String> subjects = subjectsService.getAll();
        if (subjects.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(subjects);
        }
    }

    @PostMapping("/{name}")
    @AuthRole(UserDTO.Role.ADMIN)
    public ResponseEntity<Object> saveSubject(@PathVariable("name") String name) {
        subjectsService.save(name);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{name}")
    @AuthRole(UserDTO.Role.ADMIN)
    public ResponseEntity<List<String>> deleteSubject(@PathVariable("name") String name) {
        List<String> teachersWithSubject = subjectsService.getTeachersWithSubject(name);
        if (teachersWithSubject.isEmpty()) {
            subjectsService.delete(name);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.badRequest().body(teachersWithSubject);
        }
    }

    // ADMINISTRATION ENDPOINT
    // TODO: move to an administration package maybe
    @DeleteMapping
    @AuthRole(UserDTO.Role.ADMIN)
    public ResponseEntity<Object> deleteAll() {
        subjectsService.deleteAll();
        return ResponseEntity.noContent().build();
    }
}
