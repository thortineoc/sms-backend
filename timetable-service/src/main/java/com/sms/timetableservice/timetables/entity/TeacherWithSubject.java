package com.sms.timetableservice.timetables.entity;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TeacherWithSubject that = (TeacherWithSubject) o;
        return Objects.equals(teacherId, that.teacherId) && Objects.equals(subject, that.subject);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teacherId, subject);
    }
}
