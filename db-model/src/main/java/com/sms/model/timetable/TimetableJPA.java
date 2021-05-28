package com.sms.model.timetable;

import javax.persistence.*;


@Entity
@Table(name = "classes")
public class TimetableJPA {

    private Long id;
    private String groups;
    private String subject;
    private String teacherId;
    private Integer weekday;
    private Integer room;
    private Integer lesson;
    private Long conflict;

    @Id
    @Column(name = "id")
    @GeneratedValue(generator = "classes_id_seq")
    public Long getId() {
        return id;
    }

    @Column(name = "conflict")
    public Long getConflict() {
        return conflict;
    }

    @Column(name = "weekday")
    public Integer getWeekday() {
        return weekday;
    }

    @Column(name = "groups")
    public String getGroups() {
        return groups;
    }

    @Column(name = "room")
    public Integer getRoom() {
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

    public void setGroups(String groups) {
        this.groups = groups;
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

    public void setConflict(Long conflict) {
        this.conflict = conflict;
    }

    public void setRoom(Integer room) {
        this.room = room;
    }

    public void setWeekday(Integer weekday) {
        this.weekday = weekday;
    }

}
