package com.sms.gradesservice.grades.control;

import com.sms.model.grades.GradeJPA;
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

    void deleteAllBySubject(String subject);

    List<GradeJPA> findAllBySubjectAndStudentIdIn(String subject, Collection<String> studentIds);

    List<GradeJPA> findAllByTeacherId(String teacherId);
}
