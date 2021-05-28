package com.sms.timetableservice.timetable.control;

import com.sms.model.timetable.TimetableJPA;
import org.hibernate.annotations.SQLInsert;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface TimetableRepository extends CrudRepository<TimetableJPA, Long> {

  /*  @Transactional
    @Modifying
    @SQLInsert(sql = "insert into classes (groups, subject, teacher_id, weekday, lesson, room, conflict) values (?,?,?,?,?,?,?)")
    void createTimetable(String groups, String subject, String teacher_id, Integer weekday, Integer room, Long conflict);*/

}
