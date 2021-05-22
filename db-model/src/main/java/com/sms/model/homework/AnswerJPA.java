package com.sms.model.homework;

import com.sms.model.grades.GradeJPA;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "answers")
public class AnswerJPA {

    private Long id;
    private String studentId;
    private String review;
    private Timestamp lastUpdatedTime;
    private Timestamp createdTime;
    private HomeworkJPA homework;
    private List<FileInfoJPA> files;
    private GradeJPA grade;

    @Id
    @GeneratedValue(generator = "answers_id_seq")
    public Long getId() {
        return id;
    }

    @Column(name = "student_id")
    public String getStudentId() {
        return studentId;
    }

    @Column(name = "review")
    public String getReview() {
        return review;
    }

    @Column(name = "lastupdatedtime")
    public Timestamp getLastUpdatedTime() {
        return lastUpdatedTime;
    }

    @Column(name = "createdtime", updatable = false)
    public Timestamp getCreatedTime() {
        return createdTime;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    public HomeworkJPA getHomework() {
        return homework;
    }

    @OneToOne(fetch = FetchType.LAZY)
    public GradeJPA getGrade() {
        return grade;
    }

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "relation_id")
    @Where(clause = "type = 'ANSWER'")
    public List<FileInfoJPA> getFiles() {
        return files;
    }

    public void setFiles(List<FileInfoJPA> files) {
        this.files = files;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCreatedTime(Timestamp createdTime) {
        this.createdTime = createdTime;
    }

    public void setLastUpdatedTime(Timestamp lastUpdatedTime) {
        this.lastUpdatedTime = lastUpdatedTime;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public void setHomework(HomeworkJPA homework) {
        this.homework = homework;
    }

    public void setCreatedTime(LocalDateTime localDateTime) {
        this.createdTime =  Timestamp.valueOf(localDateTime);
    }

    public void setGrade(GradeJPA grade) {this.grade=grade;}

    public void setLastUpdatedTime(LocalDateTime localDateTime) {
        this.lastUpdatedTime =  Timestamp.valueOf(localDateTime);
    }
}
