package com.sms.homeworkservice.homeworks.control.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface HomeworksFilesRepository extends CrudRepository<FileJPA, Long> {

    @NonNull
    List<FileJPA> findAll();

    List<FileJPA> findAllById(Long id);

    List<FileJPA> findAllByHomeworkid(Integer homeworkID);

}
