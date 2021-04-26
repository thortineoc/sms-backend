package com.sms.gradesservice.grades.control.repository;

import org.springframework.data.repository.CrudRepository;

import java.util.Collection;
import java.util.List;

public interface GradesRepository extends CrudRepository<Long, GradeJPA> {

    List<GradeJPA> findAllByStudentId(String studentId);

    List<GradeJPA> findAllBySubjectAndStudentIdIn(String subject, Collection<String> studentIds);
}
