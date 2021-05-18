package com.sms.homeworkservice.answer.control;

import com.sms.model.homework.AnswerJPA;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import java.util.List;

@Transactional
public interface AnswerRepository extends CrudRepository<AnswerJPA, Long> {

    Boolean existsByHomeworkId(Long id);

    Optional<AnswerJPA> findByStudentIdAndHomeworkId(String studentId, Long homeworkId);
    List<AnswerJPA> findAllByHomeworkId(Long id);
    Integer deleteByHomeworkId(Long id);
}
