package com.sms.homeworkservice.homework.control;

import com.sms.model.homework.HomeworkJPA;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional
public interface HomeworkRepository extends CrudRepository<HomeworkJPA, Long> {

    @Query("SELECT h FROM HomeworkJPA h JOIN FETCH h.answers WHERE h.id = (:id)")
    Optional<HomeworkJPA> getHomeworkDetails(Long id);

    List<HomeworkJPA> getAllByTeacherId(String teacherId);

    List<HomeworkJPA> getAllByGroup(String group);
}
