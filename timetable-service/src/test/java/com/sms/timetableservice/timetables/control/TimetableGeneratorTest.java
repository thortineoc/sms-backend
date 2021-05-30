package com.sms.timetableservice.timetables.control;

import com.google.common.collect.*;
import com.sms.api.common.BadRequestException;
import com.sms.api.timetables.TimetableConfigDTO;
import com.sms.timetableservice.timetables.entity.ClassJPA;
import com.sms.timetableservice.timetables.entity.LessonKey;
import com.sms.timetableservice.timetables.entity.TeacherWithSubject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class TimetableGeneratorTest {

    @Test
    void shouldGenerateCorrectTimetableWithoutConflicts() {
        // GIVEN
        TimetableConfigDTO config = TimetableConfigDTO.builder()
                .config(Collections.emptyList())
                .lessonCount(7).build();
        Multiset<TeacherWithSubject> subjects = getSubjects(ImmutableMap.of(
                "teacher_1", ImmutableMap.of("Math", 2, "Physics", 1),
                "teacher_2", ImmutableMap.of("Biology", 1, "PE", 2)));
        byte[][] layout = {
                {1, 1, 1, 1, 1},
                {1, 0, 0, 0, 0}};
        // WHEN
        Map<LessonKey, ClassJPA> classes = new TimetableGenerator("1A", config, subjects, Collections.emptyMap())
                .generate();

        // THEN
        Assertions.assertEquals(6, classes.size());
        assertThat(classes.values()).filteredOn(c -> "teacher_1".equals(c.getTeacherId())).hasSize(3);
        assertThat(classes.values()).filteredOn(c -> "teacher_2".equals(c.getTeacherId())).hasSize(3);
        assertThat(classes.values()).allMatch(c -> "1A".equals(c.getGroup()));
        assertThat(classes.values()).filteredOn(c -> "Math".equals(c.getSubject())).hasSize(2);
        assertThat(classes.values()).filteredOn(c -> "Physics".equals(c.getSubject())).hasSize(1);
        assertThat(classes.values()).filteredOn(c -> "Biology".equals(c.getSubject())).hasSize(1);
        assertThat(classes.values()).filteredOn(c -> "PE".equals(c.getSubject())).hasSize(2);
        assertLayout(classes, layout);
    }

    @Test
    void shouldThrowExceptionWhenClassesWontFit() {
        // GIVEN
        TimetableConfigDTO config = TimetableConfigDTO.builder()
                .config(Collections.emptyList())
                .lessonCount(1).build();
        Multiset<TeacherWithSubject> subjects = getSubjects(ImmutableMap.of(
                "teacher_1", ImmutableMap.of("Math", 2, "Physics", 1),
                "teacher_2", ImmutableMap.of("Biology", 1, "PE", 2)));

        // WHEN
        TimetableGenerator generator = new TimetableGenerator("1A", config, subjects, Collections.emptyMap());

        // THEN
        Assertions.assertThrows(BadRequestException.class, generator::generate);
    }

    @Test
    void shouldGenerateTimetableWhenConflictsArePossible() {
        // GIVEN
        TimetableConfigDTO config = TimetableConfigDTO.builder()
                .config(Collections.emptyList())
                .lessonCount(7).build();
        Map<String, Map<LessonKey, ClassJPA>> potentialConflicts = ImmutableMap.of(
                "teacher_1", ImmutableMap.of(
                        new LessonKey(3, 0), getLesson(1L, ""),
                        new LessonKey(1, 1), getLesson(3L, ""),
                        new LessonKey(0, 1), getLesson(2L, "")));
        Multiset<TeacherWithSubject> subjects = getSubjects(ImmutableMap.of(
                "teacher_1", ImmutableMap.of("Math", 9, "Physics", 6)));
        byte[][] layout = {
                {0, 0, 1, 0, 1},
                {0, 0, 1, 1, 1},
                {1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1}};

        // WHEN
        Map<LessonKey, ClassJPA> classes = new TimetableGenerator("1A", config, subjects, potentialConflicts)
                .generate();

        // THEN
        Assertions.assertEquals(15, classes.size());
        assertThat(classes.values()).filteredOn(c -> "teacher_1".equals(c.getTeacherId())).hasSize(15);
        assertThat(classes.values()).allMatch(c -> "1A".equals(c.getGroup()));
        assertThat(classes.values()).filteredOn(c -> "Math".equals(c.getSubject())).hasSize(9);
        assertThat(classes.values()).filteredOn(c -> "Physics".equals(c.getSubject())).hasSize(6);
        assertLayout(classes, layout);
    }

    private void assertLayout(Map<LessonKey, ClassJPA> classes, byte[][] layout) {
        for (int lesson = 0; lesson < layout.length; lesson++) {
            for (int day = 0; day < 5; day++) {
                boolean isPresent = layout[lesson][day] == 1;
                if (isPresent) {
                    Assertions.assertTrue(classes.containsKey(new LessonKey(day, lesson)));
                } else {
                    Assertions.assertFalse(classes.containsKey(new LessonKey(day, lesson)));
                }
            }
        }
    }

    private Multiset<TeacherWithSubject> getSubjects(Map<String, Map<String, Integer>> subjects) {
        Multiset<TeacherWithSubject> set = HashMultiset.create();
        subjects.forEach((teacher, subject) -> {
            for (Map.Entry<String, Integer> entry : subject.entrySet()) {
                for (int i = 0; i < entry.getValue(); i++) {
                    set.add(new TeacherWithSubject(teacher, entry.getKey()));
                }
            }
        });
        return set;
    }

    private ClassJPA getLesson(Long id, String conflicts) {
        ClassJPA jpa = new ClassJPA();
        jpa.setConflicts(conflicts);
        jpa.setId(id);
        return jpa;
    }
}
