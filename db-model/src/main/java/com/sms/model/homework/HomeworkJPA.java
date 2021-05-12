package com.sms.model.homework;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "homeworks")
public class HomeworkJPA {

    @Id
    private Long id;
    private String title;
    private String description;

    @Column(name = "groups")
    private String group;
    private String subject;
    private Timestamp deadline;

    @Column(name = "createdtime")
    private Timestamp createdTime;

    @Column(name = "lastupdatedtime")
    private Timestamp lastUpdatedTime;

    @Column(name = "teacher_id")
    private String teacherId;

    @Column(name = "toevaluate")
    private Boolean toEvaluate;

    @OneToMany(cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    @JoinColumn(name = "homework_id")
    private List<AnswerJPA> answers;

    @OneToMany(cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    @JoinColumn(name = "homework_id")
    private List<HomeworkFileJPA> files;

    public Timestamp getCreatedTime() { return createdTime; }
    public String getTeacherId() { return teacherId; }
    public String getSubject() { return subject; }
    public String getDescription() { return description; }
    public Boolean getToEvaluate() { return toEvaluate; }
    public Long getId() { return id; }
    public String getGroup() { return group; }
    public String getTitle() { return title; }
    public Timestamp getDeadline() { return deadline; }
    public Timestamp getLastUpdatedTime() { return lastUpdatedTime; }
    public List<AnswerJPA> getAnswers() { return answers; }
    public List<HomeworkFileJPA> getFiles() { return files; }

    public void setCreatedTime(Timestamp createdTime) { this.createdTime = createdTime; }
    public void setTeacherId(String teacherId) { this.teacherId = teacherId; }
    public void setSubject(String subject) { this.subject = subject; }
    public void setDescription(String description) { this.description = description; }
    public void setDeadline(Timestamp deadline) { this.deadline = deadline; }
    public void setGroup(String group) { this.group = group; }
    public void setId(Long id) { this.id = id; }
    public void setLastUpdatedTime(Timestamp lastUpdatedTime) { this.lastUpdatedTime = lastUpdatedTime; }
    public void setTitle(String title) { this.title = title; }
    public void setToEvaluate(Boolean toEvaluate) { this.toEvaluate = toEvaluate; }
}
