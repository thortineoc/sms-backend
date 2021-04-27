package com.sms.gradesservice.grades.control.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Transactional
public interface GradesRepository extends CrudRepository<GradeJPA, Long> {

    List<GradeJPA> findAllByStudentId(String studentId);

    List<GradeJPA> findAllBySubjectAndStudentIdIn(String subject, Collection<String> studentIds);
}
