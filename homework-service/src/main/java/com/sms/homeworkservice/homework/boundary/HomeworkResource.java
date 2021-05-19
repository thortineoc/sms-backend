package com.sms.homeworkservice.homework.boundary;

import com.sms.api.homework.HomeworkDTO;
import com.sms.api.homework.SimpleHomeworkDTO;
import com.sms.api.usermanagement.UserDTO;
import com.sms.context.AuthRole;
import com.sms.context.UserContext;
import com.sms.homeworkservice.homework.control.HomeworkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/homework")
@Scope("request")
public class HomeworkResource {

    @Autowired
    UserContext userContext;

    @Autowired
    HomeworkService homeworkService;

    @GetMapping("/{id}")
    @AuthRole({UserDTO.Role.STUDENT, UserDTO.Role.PARENT, UserDTO.Role.TEACHER})
    public ResponseEntity<SimpleHomeworkDTO> getDetailHomework(@PathVariable("id") Long id) {
        Optional<SimpleHomeworkDTO> result = homeworkService.getDetails(id);
        return result.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.noContent().build());
    }

    @GetMapping("/teacher")
    @AuthRole(UserDTO.Role.TEACHER)
    public ResponseEntity<Map<String, Map<String, List<SimpleHomeworkDTO>>>> getHomeworks() {
        Map<String, Map<String, List<SimpleHomeworkDTO>>> result = homeworkService.getListForTeacher();
        return result.isEmpty()
            ? ResponseEntity.noContent().build()
            : ResponseEntity.ok(result);
    }

    @GetMapping("/student")
    @AuthRole({UserDTO.Role.STUDENT, UserDTO.Role.PARENT})
    public ResponseEntity<Map<String, List<SimpleHomeworkDTO>>> getListForStudents() {
        Map<String, List<SimpleHomeworkDTO>> result = homeworkService.getListForStudentAndParent();
        return result.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(result);
    }

    @PutMapping
    @AuthRole(UserDTO.Role.TEACHER)
    public ResponseEntity<HomeworkDTO> updateHomework(@RequestBody HomeworkDTO homeworkDTO) {
        HomeworkDTO homework = homeworkService.updateHomework(homeworkDTO);
        return ResponseEntity.ok(homework);

    }

    @DeleteMapping("/{id}")
    @AuthRole(UserDTO.Role.TEACHER)
    public ResponseEntity<Object> deleteHomework(@PathVariable("id") Long id) {
        homeworkService.deleteHomework(id);
        return ResponseEntity.noContent().build();
    }

}
