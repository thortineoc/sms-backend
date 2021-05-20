package com.sms.model.timetable;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "classes")
public class TimetableJPA {

    @Id
    @GeneratedValue(generator = "timetables_id_seq")
    @Column(name = "id")
    Long id;

    @Column(name = "groups")
    String group;

    @Column(name = "teacher_id")
    Long teacherId;

    @Column(name = "weekday")
    Integer weekday;

    @Column(name = "room")
    Integer room;

    @Column(name = "begindate")
    Timestamp begindate;

    @Column(name = "enddate")
    Timestamp enddate;

    @Column(name = "lesson")
    Integer lesson;

    public String getGroup() { return group; }

    public void setGroup(String group) { this.group = group; }

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public Long getTeacherId() { return teacherId; }

    public void setTeacherId(Long teacherId) { this.teacherId = teacherId; }

    public Integer getWeekday() { return weekday; }

    public void setWeekday(Integer weekday) { this.weekday = weekday; }

    public Integer getRoom() { return room; }

    public void setRoom(Integer room) { this.room = room; }

    public Timestamp getBegindate() { return begindate; }

    public void setBegindate(Timestamp begindate) { this.begindate = begindate; }

    public Timestamp getEnddate() { return enddate; }

    public void setEnddate(Timestamp enddate) { this.enddate = enddate; }

    public Integer getLesson() { return lesson; }

    public void setLesson(Integer lesson) { this.lesson = lesson; }
}
