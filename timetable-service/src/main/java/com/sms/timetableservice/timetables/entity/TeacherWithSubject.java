package com.sms.timetableservice.timetables.entity;

public class TeacherWithSubject {

    private final String teacherId;
    private final String subject;

    public TeacherWithSubject(String teacherId, String subject) {
        this.teacherId = teacherId;
        this.subject = subject;
    }

    public String getTeacherId() {
        return teacherId;
    }

    public String getSubject() {
        return subject;
    }
}
