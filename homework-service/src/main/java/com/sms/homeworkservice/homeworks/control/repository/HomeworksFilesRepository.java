package com.sms.homeworkservice.homeworks.control.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface HomeworksFilesRepository extends CrudRepository<HomeworkFilesJPA, Long> {

    @NonNull
    List<HomeworkFilesJPA> findAll();

    List<HomeworkFilesJPA> findAllById(Long id);

    List<HomeworkFilesJPA> findAllByHomeworkid(Integer homeworkID);

}
