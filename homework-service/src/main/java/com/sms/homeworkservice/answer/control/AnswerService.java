package com.sms.homeworkservice.answer.control;

import com.sms.api.homework.AnswerDTO;
import com.sms.context.UserContext;
import com.sms.homeworkservice.clients.UserManagementClient;
import com.sms.homeworkservice.homework.control.HomeworkRepository;
import com.sms.model.homework.AnswerJPA;
import com.sms.model.homework.HomeworkJPA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
@Scope("request")
public class AnswerService {
    @Autowired
    AnswerRepository answerRepository;

    @Autowired
    HomeworkRepository homeworkRepository;

    @Autowired
    UserContext userContext;

    @Autowired
    UserManagementClient userManagementClient;

    public void createAnswer(AnswerDTO answer, Long homeworkId) {
        HomeworkJPA homework = homeworkRepository.findById(homeworkId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        AnswerJPA answerJPA = AnswerMapper.toJPA(answer);
        answerJPA.setHomework(homework);
        answerJPA.setStudentId(userContext.getUserId());
        answerRepository.save(answerJPA);
    }
}
