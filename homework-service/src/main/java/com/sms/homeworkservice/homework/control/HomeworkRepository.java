package com.sms.homeworkservice.homework.control;

import com.sms.model.homework.HomeworkJPA;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Transactional
public interface HomeworkRepository extends CrudRepository<HomeworkJPA, Long> {

    @Query("SELECT h FROM HomeworkJPA h WHERE h.id = (:id)")
    Optional<HomeworkJPA> getHomeworkDetails(Long id);

    List<HomeworkJPA> getAllByTeacherId(String teacherId);

    List<HomeworkJPA> getAllByGroup(String group);

    Optional<HomeworkJPA> getById(Long id);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE HomeworkJPA u SET u.deadline = :deadline," +
            " u.group= :group, u.subject=:subject, u.description=:description," +
            " u.title=:title, u.toEvaluate=:evaluate WHERE u.id=:id")
    int updateTable(Timestamp deadline, String group, String subject, Long id, Optional<String> description, String title, Boolean evaluate );


}
