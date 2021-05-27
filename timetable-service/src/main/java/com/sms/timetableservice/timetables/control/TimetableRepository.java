package com.sms.timetableservice.timetables.control;

import com.sms.timetableservice.timetables.entity.ClassJPA;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
public interface TimetableRepository extends CrudRepository<ClassJPA, Long> {

    List<ClassJPA> findAllByTeacherIdIn(List<String> teacherIds);

    @Modifying
    int deleteAllByGroup(String group);
}
