package com.sms.timetableservice.timetables.control;

import com.sms.api.common.NotFoundException;
import com.sms.timetableservice.timetables.entity.ClassJPA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Component
@Scope("request")
public class TimetableCreateService {

    @Autowired
    TimetableRepository timetableRepository;

    @Autowired
    TimetableCommonService commonService;

    @Transactional
    public void moveLesson(Long id, Integer day, Integer lesson) {
        ClassJPA jpa = timetableRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Lesson with id: " + id + " doesn't exist."));

        if (jpa.getWeekday().equals(day) && jpa.getLesson().equals(lesson)) {
            return;
        }
        Set<ClassJPA> classesToUpdate = new HashSet<>(commonService.removeFromConflicts(jpa));
        if (1 != timetableRepository.moveClass(id, day, lesson)) {
            throw new IllegalStateException("Couldn't move lesson with id: "
                    + id + " to day: " + day + " and lesson: " + lesson);
        }
        classesToUpdate.addAll(commonService.addToConflicts(jpa));
        timetableRepository.saveAll(classesToUpdate);
        timetableRepository.updateConflicts(jpa.getConflicts(), jpa.getId());
    }
}
