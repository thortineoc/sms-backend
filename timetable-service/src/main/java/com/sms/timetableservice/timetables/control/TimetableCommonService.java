package com.sms.timetableservice.timetables.control;

import com.google.common.base.Strings;
import com.sms.timetableservice.timetables.entity.ClassJPA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Scope("request")
public class TimetableCommonService {

    @Autowired
    TimetableRepository timetableRepository;

    void removeConflictIds(ClassJPA jpa, Set<Long> ids) {
        Set<Long> conflicts = getConflictIds(jpa);
        conflicts.removeAll(ids);
        String joinedConflicts = getIdsAsString(conflicts);
        jpa.setConflicts(joinedConflicts);
    }

    List<ClassJPA> removeFromConflicts(ClassJPA jpa) {
        Set<Long> conflictIds = getConflictIds(jpa);
        if (!conflictIds.isEmpty()) {
            List<ClassJPA> conflicts = timetableRepository.findAllByIdIn(conflictIds);
            conflicts.forEach(c -> removeConflictIds(c, Collections.singleton(jpa.getId())));
            return conflicts;
        } else {
            return Collections.emptyList();
        }
    }

    List<ClassJPA> addToConflicts(ClassJPA jpa) {
        List<ClassJPA> conflicts = timetableRepository
                .findAllByWeekdayAndLessonAndTeacherId(jpa.getWeekday(), jpa.getLesson(), jpa.getTeacherId());
        String conflictIds = getIdsAsString(conflicts);
        jpa.setConflicts(conflictIds);
        addConflictId(conflicts, jpa.getId());
        return conflicts;
    }

    void addConflictId(List<ClassJPA> jpa, Long id) {
        jpa.forEach(c -> {
            Set<Long> ids = getConflictIds(c);
            ids.add(id);
            String newConflicts = getIdsAsString(ids);
            c.setConflicts(newConflicts);
        });
    }

    public Set<Long> getConflictIds(List<ClassJPA> classes) {
        return classes.stream()
                .map(ClassJPA::getConflicts)
                .filter(Objects::nonNull)
                .flatMap(c -> Arrays.stream(c.split(",")).map(Long::valueOf))
                .collect(Collectors.toSet());
    }

    public Set<Long> getConflictIds(ClassJPA jpa) {
        String conflicts = jpa.getConflicts();
        if (!Strings.isNullOrEmpty(conflicts)) {
            return Arrays.stream(conflicts.split(",")).map(Long::valueOf).collect(Collectors.toSet());
        } else {
            return Collections.emptySet();
        }
    }

    public String getIdsAsString(Set<Long> ids) {
        String result = ids.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        return "".equals(result)
                ? null
                : result;
    }

    public String getIdsAsString(List<ClassJPA> classes) {
        String result = classes.stream()
                .map(ClassJPA::getId)
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        return "".equals(result)
                ? null
                : result;
    }
}
