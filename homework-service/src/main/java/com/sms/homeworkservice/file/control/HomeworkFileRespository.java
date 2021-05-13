package com.sms.homeworkservice.file.control;

import com.sms.model.grades.GradeJPA;
import com.sms.model.homework.FileDetailJPA;
import com.sms.model.homework.FileJPA;
import com.sms.model.homework.HomeworkFileDetailJPA;
import com.sms.model.homework.HomeworkFileJPA;
import org.springframework.data.jpa.repository.Query;
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
