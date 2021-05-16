package com.sms.homeworkservice.answer.boundary;

import com.sms.api.homework.AnswerDTO;
import com.sms.api.usermanagement.UserDTO;
import com.sms.context.AuthRole;
import com.sms.context.UserContext;
import com.sms.homeworkservice.answer.control.AnswerService;
import com.sms.homeworkservice.homework.control.HomeworkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<Object> saveAnswer(@PathVariable("id") Long id, AnswerDTO answer) {
        answerService.createAnswer(answer, id);
        return ResponseEntity.noContent().build();
    }
}
