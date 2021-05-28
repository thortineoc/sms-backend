package com.sms.timetableservice.timetables.control;

import com.sms.api.common.BadRequestException;
import com.sms.api.timetables.TimetableConfigDTO;
import com.sms.timetableservice.timetables.entity.ClassJPA;
import com.sms.timetableservice.timetables.entity.LessonKey;
import com.sms.timetableservice.timetables.entity.TeacherWithSubject;

import java.util.*;

public class TimetableGenerator {

    public static final int DAYS = 5;

    private final Map<String, Map<LessonKey, ClassJPA>> potentialConflicts;
    private final Map<LessonKey, ClassJPA> generatedClasses = new HashMap<>();
    private final Set<TeacherWithSubject> subjects;
    private final TimetableConfigDTO config;
    private final String group;

    public TimetableGenerator(String group, TimetableConfigDTO config, Set<TeacherWithSubject> subjects,
                              Map<String, Map<LessonKey, ClassJPA>> potentialConflicts) {
        this.potentialConflicts = potentialConflicts;
        this.subjects = new HashSet<>(subjects);
        this.config = config;
        this.group = group;
    }

    public List<ClassJPA> generate() {

        done: for (int lesson = 0; lesson < config.getLessonCount(); lesson++) {
            for (int day = 0; day < DAYS; day++) {
                if (subjects.isEmpty()) break done;

                LessonKey key = new LessonKey(day, lesson);

                Optional<TeacherWithSubject> subject = getFirstSubject(key);
                if (!subject.isPresent()) {
                    checkForGaps(key);
                    continue;
                }
                subjects.remove(subject.get());

                ClassJPA newClass = buildClass(subject.get(), group, key);
                generatedClasses.put(key, newClass);
            }
        }

        if (!subjects.isEmpty()) {
            throw new BadRequestException("Couldn't generate timetable, subjects: " + subjects + " won't fit");
        }

        return new ArrayList<>(generatedClasses.values());
    }

    private void checkForGaps(LessonKey current) {
        if (generatedClasses.containsKey(LessonKey.lessonBefore(current))) {
            throw new BadRequestException("Cannot generate timetable, gaps between lessons are not allowed");
        }
    }

    private Optional<TeacherWithSubject> getFirstSubject(LessonKey key) {
        List<TeacherWithSubject> listSubjects = new ArrayList<>(subjects);
        Collections.shuffle(listSubjects);
        return listSubjects.stream()
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
