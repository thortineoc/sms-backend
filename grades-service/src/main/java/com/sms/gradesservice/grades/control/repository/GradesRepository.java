package com.sms.gradesservice.grades.control.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Transactional
public interface GradesRepository extends CrudRepository<GradeJPA, Long> {

    @NonNull
    List<GradeJPA> findAll();

    List<GradeJPA> findAllByStudentId(String studentId);

    void deleteAllByStudentId(String studentId);

    List<GradeJPA> findAllBySubjectAndStudentIdIn(String subject, Collection<String> studentIds);

    List<GradeJPA> findAllByTeacherId(String teacherId);
}
