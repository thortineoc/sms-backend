package com.sms.tests.timetables;

import com.google.common.collect.Sets;
import com.sms.api.timetables.LessonDTO;
import com.sms.api.timetables.TimetableDTO;
import com.sms.api.usermanagement.UserDTO;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TimetablesAssert {

    private final Response response;
    private Map<String, LessonDTO> lessons;
    private Map<String, UserDTO> teachers;

    public TimetablesAssert(Response response) {
        this.response = response;
    }

    public LessonDTO getLessonAt(int day, int lesson) {
        return lessons.get(day + ":" + lesson);
    }

    public TimetablesAssert unwrapTimetable() {
        response.then().statusCode(200);
        TimetableDTO timetable = response.as(TimetableDTO.class);
        lessons = timetable.getLessons().stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(this::getLessonKey, Function.identity()));
        teachers = timetable.getTeachers();
        return this;
    }

    public TimetablesAssert hasTeachers(String... teacherIds) {
        Assertions.assertEquals(Sets.newHashSet(teacherIds), teachers.keySet());
        return this;
    }

    public TimetablesAssert hasLessonsWithTeacher(String teacherId, long amount) {
        long count = lessons.values().stream()
                .filter(l -> l.getTeacherId().map(teacherId::equals).orElse(false))
                .count();
        Assertions.assertEquals(amount, count);
        return this;
    }

    public TimetablesAssert hasSubject(String subject, long amount) {
        long count = lessons.values().stream()
                .filter(l -> l.getSubject().equals(subject))
                .count();
        Assertions.assertEquals(amount, count);
        return this;
    }

    public TimetablesAssert hasLayout(byte[][] expected) {
        Set<String> lessonKeys = new HashSet<>();
        for (int lesson = 0; lesson < expected.length; lesson++) {
            for (int day = 0; day < 5; day++) {
                if (expected[lesson][day] == 1) {
                    lessonKeys.add(day + ":" + lesson);
                }
            }
        }
        Assertions.assertEquals(lessonKeys, lessons.keySet());
        return this;
    }

    private String getLessonKey(LessonDTO lesson) {
        return lesson.getWeekday() + ":" + lesson.getLesson();
    }
}
