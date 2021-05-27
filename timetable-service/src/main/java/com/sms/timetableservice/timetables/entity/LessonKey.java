package com.sms.timetableservice.timetables.entity;

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

    public int getLesson() {
        return lesson;
    }

    public int getWeekday() {
        return weekday;
    }
}
