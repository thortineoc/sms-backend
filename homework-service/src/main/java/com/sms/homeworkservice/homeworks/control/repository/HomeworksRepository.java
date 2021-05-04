package com.sms.homeworkservice.homeworks.control.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface HomeworksRepository extends  CrudRepository<HomeworkJPA, Long>{

        @NonNull
        List<HomeworkJPA> findAll();

        List<HomeworkJPA> findAllByGroup(String group);

        List<HomeworkJPA> findAllByTeacherId(String teacherId);

}
