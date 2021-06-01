package com.sms.timetableservice.timetables.control;

import com.sms.timetableservice.timetables.entity.ClassJPA;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Transactional
public interface TimetableRepository extends CrudRepository<ClassJPA, Long> {

    List<ClassJPA> findAllByTeacherIdIn(Collection<String> teacherIds);

    List<ClassJPA> findAllByTeacherId(String teacherId);

    List<ClassJPA> findAllByGroup(String group);

    List<ClassJPA> findAllBySubject(String subject);

    List<ClassJPA> findAllByIdIn(Set<Long> ids);

    @Query("SELECT c FROM ClassJPA c WHERE c.conflicts IS NOT NULL")
    List<ClassJPA> findAllConflicted();

    @Query("SELECT c FROM ClassJPA c WHERE c.weekday = :weekday AND c.lesson = :lesson AND c.teacherId = :teacherId")
    List<ClassJPA> findConflicts(Integer weekday, Integer lesson, String teacherId);

    @Modifying
    @Query("UPDATE ClassJPA c SET c.weekday = :day, c.lesson = :lesson WHERE c.id = :id")
    int moveClass(Long id, Integer day, Integer lesson);

    @Modifying
    @Query("UPDATE ClassJPA c SET c.conflicts = :conflicts WHERE c.id = :id")
    int updateConflicts(String conflicts, Long id);

    @Modifying
    int deleteAllByGroup(String group);

    @Modifying
    int deleteAllBySubject(String subject);
}
