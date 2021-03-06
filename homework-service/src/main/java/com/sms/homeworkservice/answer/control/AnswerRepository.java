package com.sms.homeworkservice.answer.control;

import com.sms.model.homework.AnswerJPA;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional
public interface AnswerRepository extends CrudRepository<AnswerJPA, Long> {

    Boolean existsByHomeworkId(Long id);

    Optional<AnswerJPA> findByStudentIdAndHomeworkId(String studentId, Long homeworkId);

    List<AnswerJPA> findAllByHomeworkId(Long id);

    void deleteAllByStudentId(String id);

    void deleteAllByIdIn(List<Long> ids);

    @Modifying
    @Query("DELETE FROM AnswerJPA answer WHERE answer.homework.id=:homeworkID")
    void deleteAnswerByHomeworkId(Long homeworkID);
}
