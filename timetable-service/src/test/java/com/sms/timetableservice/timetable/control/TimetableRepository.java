package com.sms.timetableservice.timetable.control;

import com.sms.model.timetable.TimetableJPA;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface TimetableRepository extends CrudRepository<TimetableJPA, Long> {

}
