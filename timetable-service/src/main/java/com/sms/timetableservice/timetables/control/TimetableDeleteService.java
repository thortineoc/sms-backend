package com.sms.timetableservice.timetables.control;

import com.sms.api.common.NotFoundException;
import com.sms.api.common.Util;
import com.sms.timetableservice.timetables.entity.Lesson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Scope("request")
public class TimetableDeleteService {

    @Autowired
    TimetableRepository timetableRepository;

    @Autowired
    TimetableCommonService commonService;

    @Transactional
    public void deleteTimetable(String group) {
        List<Lesson> lessons = TimetableMapper.toLessons(timetableRepository.findAllByGroup(group));
        List<Lesson> updatedConflicts = getAllUpdatedConflicts(lessons);
        Util.ifNotEmpty(updatedConflicts, c -> timetableRepository.saveAll(TimetableMapper.toJPAs(c)));
        Util.ifNotEmpty(lessons, l -> timetableRepository.deleteAllByGroup(group));
    }

    @Transactional
    public void deleteClassesBySubject(String subject) {
        List<Lesson> lessons = TimetableMapper.toLessons(timetableRepository.findAllBySubject(subject));
        List<Lesson> updatedConflicts = getAllUpdatedConflicts(lessons);
        Util.ifNotEmpty(updatedConflicts, c -> timetableRepository.saveAll(TimetableMapper.toJPAs(c)));
        Util.ifNotEmpty(lessons, l -> timetableRepository.deleteAllBySubject(subject));
    }

    @Transactional
    public void deleteLesson(Long id) {
        Lesson lesson = getLesson(id);
        List<Lesson> updatedConflicts = getUpdatedConflicts(lesson);
        Util.ifNotEmpty(updatedConflicts, c -> timetableRepository.saveAll(TimetableMapper.toJPAs(c)));
        timetableRepository.deleteById(id);
    }

    private List<Lesson> getAllUpdatedConflicts(List<Lesson> lessons) {
        Set<Long> idsToRemove = lessons.stream().map(Lesson::getId).collect(Collectors.toSet());
        Set<Long> conflictIds = commonService.getAllConflicts(lessons);
        if (conflictIds.isEmpty()) {
            return Collections.emptyList();
        }
        List<Lesson> conflicts = TimetableMapper.toLessons(timetableRepository.findAllByIdIn(conflictIds));
        commonService.removeFromConflicts(conflicts, idsToRemove);
        return conflicts;
    }

    private List<Lesson> getUpdatedConflicts(Lesson lesson) {
        if (lesson.getConflicts().isEmpty()) {
            return Collections.emptyList();
        }
        List<Lesson> conflicts = TimetableMapper.toLessons(timetableRepository.findAllByIdIn(lesson.getConflicts()));
        commonService.removeFromConflicts(conflicts, lesson.getId());
        return conflicts;
    }

    private Lesson getLesson(Long id) {
        return new Lesson(timetableRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Lesson with ID: " + id + " doesn't exist")));
    }
}
