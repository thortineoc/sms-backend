package com.sms.homeworkservice.homeworks.control;


import com.sms.context.UserContext;
import com.sms.homeworks.HomeworkDTO;
import com.sms.homeworkservice.homeworks.control.repository.HomeworksRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("request")
public class HomeworksService {

    @Autowired
    UserContext userContext;

    @Autowired
    HomeworksRepository homeworksRepository;

    public HomeworkDTO updateHomework(HomeworkDTO homeworkDTO) {
        return homeworkDTO;
    }
}
