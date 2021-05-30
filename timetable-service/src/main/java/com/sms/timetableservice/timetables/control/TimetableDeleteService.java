package com.sms.timetableservice.timetables.control;

import com.google.common.base.Strings;
import com.sms.api.common.NotFoundException;
import com.sms.timetableservice.timetables.entity.ClassJPA;
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
        List<ClassJPA> classes = timetableRepository.findAllByGroup(group);
        List<ClassJPA> updatedConflicts = getAllUpdatedConflicts(classes);
        if (!updatedConflicts.isEmpty()) {
            timetableRepository.saveAll(updatedConflicts);
        }

        if (!classes.isEmpty()) {
            timetableRepository.deleteAllByGroup(group);
        }
    }

    @Transactional
    public void deleteClassesBySubject(String subject) {
        List<ClassJPA> classes = timetableRepository.findAllBySubject(subject);
        List<ClassJPA> updatedConflicts = getAllUpdatedConflicts(classes);
        if (!updatedConflicts.isEmpty()) {
            timetableRepository.saveAll(updatedConflicts);
        }

        if (!classes.isEmpty()) {
            timetableRepository.deleteAllBySubject(subject);
        }
    }

    @Transactional
    public void deleteLesson(Long id) {
        Optional<ClassJPA> lesson = timetableRepository.findById(id);
        if (!lesson.isPresent()) {
            throw new NotFoundException("Lesson with ID: " + id + " doesn't exist");
        }
        List<ClassJPA> updatedConflicts = getUpdatedConflicts(lesson.get());
        if (!updatedConflicts.isEmpty()) {
            timetableRepository.saveAll(updatedConflicts);
        }
        timetableRepository.deleteById(id);
    }

    private List<ClassJPA> getAllUpdatedConflicts(List<ClassJPA> jpa) {
        Set<Long> idsToRemove = jpa.stream().map(ClassJPA::getId).collect(Collectors.toSet());
        Set<Long> conflictIds = commonService.getConflictIds(jpa);
        if (conflictIds.isEmpty()) {
            return Collections.emptyList();
        }
        List<ClassJPA> conflicts = timetableRepository.findAllByIdIn(conflictIds);
        conflicts.forEach(c -> commonService.removeConflictIds(c, idsToRemove));
        return conflicts;
    }

    private List<ClassJPA> getUpdatedConflicts(ClassJPA jpa) {
        if (Strings.isNullOrEmpty(jpa.getConflicts())) {
            return Collections.emptyList();
        }
        Set<Long> conflictIds = commonService.getConflictIds(jpa);
        List<ClassJPA> conflicts = timetableRepository.findAllByIdIn(conflictIds);
        conflicts.forEach(c -> commonService.removeConflictIds(c, Collections.singleton(jpa.getId())));
        return conflicts;
    }
}
