package com.sms.gradesservice.grades.control.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
public interface GradeRepository extends CrudRepository<GradeJPA, Integer>{

    @NonNull
    List<GradeJPA> findAll();
}
