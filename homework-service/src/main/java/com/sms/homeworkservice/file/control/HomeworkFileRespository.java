package com.sms.homeworkservice.file.control;

import com.sms.model.homework.FileDetailJPA;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional
public interface HomeworkFileRespository extends CrudRepository<FileDetailJPA, Long> {

    @NonNull
    List<FileDetailJPA> findAll();

    Optional<FileDetailJPA> findAllById(Long Id);
}
