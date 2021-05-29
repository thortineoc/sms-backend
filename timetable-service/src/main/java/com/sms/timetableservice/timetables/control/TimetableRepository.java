package com.sms.timetableservice.timetables.control;

import com.sms.timetableservice.timetables.entity.ClassJPA;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Set;

@Transactional
public interface TimetableRepository extends CrudRepository<ClassJPA, Long> {

    List<ClassJPA> findAllByTeacherIdIn(List<String> teacherIds);

    List<ClassJPA> findAllByTeacherId(String teacherId);

    List<ClassJPA> findAllByGroup(String group);

    List<ClassJPA> findAllByIdIn(Set<Long> ids);

    @Modifying
    int deleteAllByGroup(String group);
}
