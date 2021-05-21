package com.sms.homeworkservice.answer.boundary;

import com.sms.api.homework.AnswerDTO;
import com.sms.api.usermanagement.UserDTO;
import com.sms.context.AuthRole;
import com.sms.context.UserContext;
import com.sms.homeworkservice.answer.control.AnswerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/answer")
@Scope("request")
public class AnswerResource {

    @Autowired
    UserContext userContext;

    @Autowired
    AnswerService answerService;

    @AuthRole(UserDTO.Role.STUDENT)
    @PostMapping("/{id}") // homework_id
    public ResponseEntity<AnswerDTO> createAnswer(@PathVariable("id") Long id) {
        AnswerDTO res = answerService.createAnswer(id);
        return ResponseEntity.ok(res);
    }

    @AuthRole(UserDTO.Role.TEACHER)
    @PutMapping()
    public ResponseEntity<AnswerDTO> updateAnswer(@RequestBody AnswerDTO answer) {
        AnswerDTO res = answerService.updateAnswer(answer);
        return ResponseEntity.ok(res);
    }
}
