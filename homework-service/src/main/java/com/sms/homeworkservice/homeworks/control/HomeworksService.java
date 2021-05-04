package com.sms.homeworkservice.homeworks.control;


import com.sms.context.UserContext;
import com.sms.homeworks.HomeworkDTO;
import com.sms.homeworkservice.homeworks.control.repository.HomeworkJPA;
import com.sms.homeworkservice.homeworks.control.repository.HomeworksRepository;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Component
@Scope("request")
public class HomeworksService {

    @Autowired
    UserContext userContext;

    @Autowired
    HomeworksRepository homeworksRepository;

    public void updateHomework(HomeworkDTO homeworkDTO) {
        HomeworkJPA homework = HomeworkMapper.toJPA(homeworkDTO);
        homework.setTeacherid(userContext.getUserId());

        List<HomeworkJPA> homeworkJPAList = homeworksRepository.findAll();
        try {
            HomeworkJPA updatedHomework = homeworksRepository.save(homework);
        } catch (ConstraintViolationException e) {
            throw new IllegalArgumentException("Saving grade: " + homework.getId() + " violated database constraints: " + e.getConstraintName());
        } catch (EntityNotFoundException e) {
            throw new IllegalStateException("Grade with ID: " + homework.getId() + " does not exist, can't update: " + e.getMessage());
        } catch (Exception e){
            throw new IllegalStateException(e);
        }

    }
    }

