package com.sms.timetableservice.timetables.control;

import com.sms.timetableservice.timetables.entity.Lesson;
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

    void removeFromConflicts(Collection<Lesson> conflicts, Long id) {
        conflicts.forEach(c -> c.getConflicts().remove(id));
    }

    void removeFromConflicts(Collection<Lesson> conflicts, Set<Long> ids) {
        conflicts.forEach(c -> c.getConflicts().removeAll(ids));
    }

    void addToConflicts(Collection<Lesson> conflicts, Long id) {
        conflicts.forEach(c -> c.getConflicts().add(id));
    }

    Set<Long> getAllConflicts(Collection<Lesson> lessons) {
        return lessons.stream().flatMap(l -> l.getConflicts().stream()).collect(Collectors.toSet());
    }
}
