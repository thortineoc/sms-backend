package com.sms.timetableservice.timetables.entity;

import javax.persistence.*;

@Entity
@Table(name = "classes")
public class ClassJPA {

    private Long id;
    private String group;
    private String subject;
    private String teacherId;
    private Integer weekday;
    private String room;
    private Integer lesson;
    private String conflicts;

    @Id
    @Column(name = "id")
    @GeneratedValue(generator = "classes_id_seq")
    public Long getId() {
        return id;
    }

    @Column(name = "conflict")
    public String getConflicts() {
        return conflicts;
    }

    @Column(name = "weekday")
    public Integer getWeekday() {
        return weekday;
    }

    @Column(name = "groups")
    public String getGroup() {
        return group;
    }

    @Column(name = "room")
    public String getRoom() {
        return room;
    }

    @Column(name = "subject")
    public String getSubject() {
        return subject;
    }

    @Column(name = "teacher_id")
    public String getTeacherId() {
        return teacherId;
    }

    @Column(name = "lesson")
    public Integer getLesson() {
        return lesson;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }

    public void setLesson(Integer lesson) {
        this.lesson = lesson;
    }

    public void setConflicts(String conflicts) {
        this.conflicts = conflicts;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public void setWeekday(Integer weekday) {
        this.weekday = weekday;
    }
}
