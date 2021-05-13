package com.sms.homeworkservice.file.control;

import com.sms.model.homework.HomeworkFileDetailJPA;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional
public interface HomeworkFileRespository extends CrudRepository<HomeworkFileDetailJPA, Long>{

    @NonNull
    List<HomeworkFileDetailJPA> findAll();
    Optional<HomeworkFileDetailJPA> findAllById(Long Id);
}
