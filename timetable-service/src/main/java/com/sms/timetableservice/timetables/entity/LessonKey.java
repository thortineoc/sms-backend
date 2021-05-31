package com.sms.timetableservice.timetables.entity;

import com.sms.api.timetables.LessonDTO;
import com.sms.api.timetables.LessonsDTO;

import java.util.Objects;

public class LessonKey {

    private final int weekday;
    private final int lesson;

    public LessonKey(int weekday, int lesson) {
        this.weekday = weekday;
        this.lesson = lesson;
    }

    public LessonKey(ClassJPA c) {
        this.weekday = c.getWeekday();
        this.lesson = c.getLesson();
    }

    public LessonKey(LessonDTO c) {
        this.weekday = c.getWeekDay();
        this.lesson = c.getLesson();
    }

    public static LessonKey lessonBefore(LessonKey key) {
        return new LessonKey(key.getWeekday(), Math.max(key.getLesson() - 1, 0));
    }

    public int getLesson() {
        return lesson;
    }

    public int getWeekday() {
        return weekday;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LessonKey lessonKey = (LessonKey) o;
        return weekday == lessonKey.weekday && lesson == lessonKey.lesson;
    }

    @Override
    public int hashCode() {
        return Objects.hash(weekday, lesson);
    }
}
