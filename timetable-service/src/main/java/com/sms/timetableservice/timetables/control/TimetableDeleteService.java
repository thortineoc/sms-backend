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
        Set<Long> conflictIds = jpa.stream()
                .map(this::getConflictIds)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
        if (conflictIds.isEmpty()) {
            return Collections.emptyList();
        }
        List<ClassJPA> conflicts = timetableRepository.findAllByIdIn(conflictIds);
        conflicts.forEach(c -> removeConflictIds(c, idsToRemove));
        return conflicts;
    }

    private List<ClassJPA> getUpdatedConflicts(ClassJPA jpa) {
        if (Strings.isNullOrEmpty(jpa.getConflicts())) {
            return Collections.emptyList();
        }
        Set<Long> conflictIds = getConflictIds(jpa);
        List<ClassJPA> conflicts = timetableRepository.findAllByIdIn(conflictIds);
        conflicts.forEach(c -> removeConflictIds(c, Collections.singleton(jpa.getId())));
        return conflicts;
    }

    void removeConflictIds(ClassJPA jpa, Set<Long> ids) {
        Set<Long> conflicts = getConflictIds(jpa);
        conflicts.removeAll(ids);
        String joinedConflicts = conflicts.stream().map(String::valueOf).collect(Collectors.joining(","));
        if (!Strings.isNullOrEmpty(joinedConflicts)) {
            jpa.setConflicts(joinedConflicts);
        } else {
            jpa.setConflicts(null);
        }
    }

    private Set<Long> getConflictIds(ClassJPA jpa) {
        return Optional.ofNullable(jpa.getConflicts())
                .map(c -> Arrays.stream(c.split(","))
                        .map(Long::valueOf)
                        .collect(Collectors.toSet()))
                .orElse(Collections.emptySet());
    }
}
