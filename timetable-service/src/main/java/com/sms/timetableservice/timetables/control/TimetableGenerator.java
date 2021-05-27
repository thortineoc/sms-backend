package com.sms.timetableservice.timetables.control;

import com.sms.api.common.BadRequestException;
import com.sms.api.timetables.TimetableConfigDTO;
import com.sms.timetableservice.timetables.entity.ClassJPA;
import com.sms.timetableservice.timetables.entity.LessonKey;
import com.sms.timetableservice.timetables.entity.TeacherWithSubject;

import java.util.*;
import java.util.stream.IntStream;

public class TimetableGenerator {

    public static final int DAYS = 5;

    private final Map<String, Map<LessonKey, ClassJPA>> potentialConflicts;
    private final List<TeacherWithSubject> subjects;
    private final TimetableConfigDTO config;
    private final String group;

    public TimetableGenerator(String group, TimetableConfigDTO config, List<TeacherWithSubject> subjects,
                              Map<String, Map<LessonKey, ClassJPA>> potentialConflicts) {
        this.potentialConflicts = potentialConflicts;
        this.subjects = subjects;
        this.config = config;
        this.group = group;
    }

    public List<ClassJPA> generate() {
        List<ClassJPA> generatedClasses = new ArrayList<>();

        IntStream.range(0, config.getLessonCount()).forEach(lesson -> {
            IntStream.range(0, DAYS).forEach(day -> {
                LessonKey key = new LessonKey(day, lesson);

                Optional<TeacherWithSubject> subject = getFirstSubject(subjects, key);
                if (!subject.isPresent()) {
                    throw new BadRequestException("Couldn't generate timetable, no teacher available on: "
                    + WeekDays.values()[day] + " lesson: " + lesson);
                }

                ClassJPA newClass = buildClass(subject.get(), group, key);
                generatedClasses.add(newClass);
            });
        });

        return generatedClasses;
    }

    private Optional<TeacherWithSubject> getFirstSubject(List<TeacherWithSubject> subjects, LessonKey key) {
        return subjects.stream()
                .filter(s -> hasNoConflict(s.getTeacherId(), key))
                .findFirst();
    }

    private boolean hasNoConflict(String teacherId, LessonKey key) {
        Map<LessonKey, ClassJPA> lessons = potentialConflicts.get(teacherId);
        if (lessons == null) {
            return true;
        }

        ClassJPA jpa = lessons.get(key);
        if (jpa == null) {
            return true;
        }
        return false;
    }

    private ClassJPA buildClass(TeacherWithSubject subject, String group, LessonKey key) {
        ClassJPA c = new ClassJPA();
        c.setWeekday(key.getWeekday());
        c.setLesson(key.getLesson());
        c.setGroup(group);
        c.setSubject(subject.getSubject());
        c.setTeacherId(subject.getTeacherId());
        return c;
    }

    private enum WeekDays {
        MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY
    }
}
