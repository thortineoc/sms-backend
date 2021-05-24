package com.sms.homeworkservice.answer.control;

import com.sms.api.grades.GradeDTO;
import com.sms.api.homework.AnswerDTO;
import com.sms.api.homework.FileLinkDTO;
import com.sms.api.usermanagement.UserDTO;
import com.sms.context.UserContext;
import com.sms.homeworkservice.file.control.FileRepository;
import com.sms.homeworkservice.homework.control.HomeworkRepository;
import com.sms.model.grades.GradeJPA;
import com.sms.model.homework.AnswerJPA;
import com.sms.model.homework.HomeworkJPA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

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
    FileRepository fileRepository;

    @Transactional
    public AnswerDTO updateAnswer(AnswerDTO answer) {
        Long id = answer.getId().orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST)
        );
        AnswerJPA answerToUpdate = answerRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND)
        );

        answerToUpdate.setLastUpdatedTime(LocalDateTime.now());
        answer.getReview().ifPresent(answerToUpdate::setReview);

        if(answer.getGrade().isPresent()) {
            GradeDTO grade = answer.getGrade().get();
            GradeJPA gradeJPA = AnswerMapper.toJPA(grade);
            answerToUpdate.setGrade(gradeJPA);
        }

        AnswerJPA ans = answerRepository.save(answerToUpdate);
        return AnswerMapper.toDTOSimple(ans);
    }

    public AnswerDTO createAnswer(Long homeworkId) {
        HomeworkJPA homework = homeworkRepository.findById(homeworkId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        AnswerJPA answerJPA = new AnswerJPA();
        answerJPA.setHomework(homework);
        answerJPA.setStudentId(userContext.getUserId());

        answerJPA.setCreatedTime(LocalDateTime.now());
        answerJPA.setLastUpdatedTime(LocalDateTime.now());

        AnswerJPA ans = answerRepository.save(answerJPA);
        return AnswerMapper.toDTOSimple(ans);
    }

    @Transactional
    public void deleteUserAnswers(String id) {
        fileRepository.deleteAllByOwnerId(id);
        answerRepository.deleteAllByStudentId(id);
    }

    @Transactional
    public void deleteAnswer(Long id) {
        AnswerJPA answer = answerRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Answer: " + id + " doesn't exist."));
        checkIfCanDelete(answer);

        fileRepository.deleteAllByRelationIdAndType(id, FileLinkDTO.Type.ANSWER.toString());
        answerRepository.deleteById(id);
    }

    private void checkIfCanDelete(AnswerJPA answer) {
        if (userContext.getSmsRole() != UserDTO.Role.ADMIN) {
            if (answer.getReview() != null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot delete answer that is already reviewed.");
            }
            if (answer.getGrade() != null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot delete answer that is already graded.");
            }
        }
    }
}
