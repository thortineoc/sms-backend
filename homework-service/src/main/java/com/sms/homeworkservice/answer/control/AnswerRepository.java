package com.sms.homeworkservice.answer.control;

import com.sms.model.homework.AnswerJPA;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface AnswerRepository extends CrudRepository<AnswerJPA, Long> {
    AnswerJPA findByStudentIdAndHomeworkId(String studentId, Long homeworkId);
}
