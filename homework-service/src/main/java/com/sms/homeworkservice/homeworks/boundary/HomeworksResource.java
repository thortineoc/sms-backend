package com.sms.homeworkservice.homeworks.boundary;


import com.sms.context.AuthRole;
import com.sms.grades.GradeDTO;
import com.sms.homeworks.HomeworkDTO;
import com.sms.homeworkservice.homeworks.control.HomeworksService;
import com.sms.homeworkservice.homeworks.control.repository.HomeworkJPA;
import com.sms.usermanagement.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/homeworks")
@Scope("request")
public class HomeworksResource {

    @Autowired
    HomeworksService homeworksService;

    @PutMapping
    @AuthRole(UserDTO.Role.TEACHER)
    public ResponseEntity<HomeworkDTO> updateHomework(@RequestBody HomeworkDTO homeworkDTO){
        HomeworkDTO updatedHomework = homeworksService.updateHomework(homeworkDTO);
        return ResponseEntity.ok(updatedHomework);
    }
}
