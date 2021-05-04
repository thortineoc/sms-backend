package com.sms.homeworkservice.homeworks.control.repository;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "homeworks")
public class HomeworkJPA {

    @Id
    @Column(name = "id")
    @GeneratedValue(generator = "homeworks_id_seq")
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "groups")
    private String group;

    @Column(name = "subject")
    private String subject;

    @Column(name = "deadline")
    private LocalDateTime deadline;

    @Column(name = "file")
    private Byte[] file;

    @Column(name = "teacher_id")
    private String teacher_id;

    @Column(name = "toevaluate")
    private Boolean toevaluate;

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setGroup(String group) { this.group = group; }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

    public void setFile(Byte[] file) {
        this.file = file;
    }

    public void setTeacher_id(String id) { this.teacher_id=id; }

    public void setToevaluate(Boolean evaluate) { this.toevaluate=evaluate; }

    //////////////////////////////////////////////////////////////////

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getGroup() {
        return group;
    }

    public String getSubject() {
        return subject;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public Byte[] getFile() {
        return file;
    }

    public String getTeacher_id(){ return teacher_id; }

    public Boolean getToevaluate() { return toevaluate; }

    ////////////////////////////////////////////////////////////////////////
    public HomeworkJPA() {

    }

}


