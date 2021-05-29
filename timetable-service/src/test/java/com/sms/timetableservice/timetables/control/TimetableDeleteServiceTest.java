package com.sms.timetableservice.timetables.control;

import com.google.common.collect.Sets;
import com.sms.timetableservice.timetables.entity.ClassJPA;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Set;

class TimetableDeleteServiceTest {

    private final TimetableDeleteService service = new TimetableDeleteService();

    @Test
    void shouldRemoveIdsFromConflictingClasses() {
        // GIVEN
        ClassJPA lesson = getLesson(1L, "1,3,4");
        Set<Long> idsToRemove = Sets.newHashSet(1L, 2L);

        // WHEN
        service.removeConflictIds(lesson, idsToRemove);

        // THEN
        Assertions.assertEquals("3,4", lesson.getConflicts());
    }

    @Test
    void shouldRemoveAllIdsFromConflictingClasses() {
        // GIVEN
        ClassJPA lesson = getLesson(1L, "1,3,4");
        Set<Long> idsToRemove = Sets.newHashSet(1L, 3L, 4L);

        // WHEN
        service.removeConflictIds(lesson, idsToRemove);

        // THEN
        Assertions.assertNull(lesson.getConflicts());
    }

    private ClassJPA getLesson(Long id, String conflicts) {
        ClassJPA jpa = new ClassJPA();
        jpa.setConflicts(conflicts);
        jpa.setId(id);
        return jpa;
    }
}
