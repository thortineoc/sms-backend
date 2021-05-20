package com.sms.homeworkservice.answer.control;

import com.sms.api.homework.AnswerDTO;
import com.sms.api.homework.FileLinkDTO;
import com.sms.context.UserContext;
import com.sms.homeworkservice.file.control.FileRepository;
import com.sms.homeworkservice.homework.control.HomeworkRepository;
import com.sms.model.homework.AnswerJPA;
import com.sms.model.homework.HomeworkJPA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Optional;

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

    public AnswerDTO updateAnswer(AnswerDTO answer) {
        Long id = answer.getId().orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST)
        );
        AnswerJPA answerToUpdate = answerRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND)
        );

        answerToUpdate.setLastUpdatedTime(LocalDateTime.now());
        answer.getReview().ifPresent(answerToUpdate::setReview);

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

    public void deleteUserAnswers(String id) {
        fileRepository.deleteAllByOwnerId(id);
        answerRepository.deleteAllByStudentId(id);
    }

    public void deleteAnswer(Long id) {
        Optional<AnswerJPA> answer= answerRepository.findById(id);
        if(answer.isPresent() && !Optional.ofNullable(answer.get().getReview()).isPresent() && !Optional.ofNullable(answer.get().getGrade()).isPresent()) {
            fileRepository.deleteAllByRelationIdAndType(id, FileLinkDTO.Type.ANSWER.toString());
            answerRepository.deleteById(id);
        }else throw new IllegalStateException("You cannot delete answer: doesnt exist || already reviewed");
    }
}
