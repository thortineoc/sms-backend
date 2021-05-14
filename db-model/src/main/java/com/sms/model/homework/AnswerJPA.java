package com.sms.model.homework;

import com.sms.model.grades.GradeJPA;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "answers")
public class AnswerJPA {

    @Id
    @GeneratedValue(generator = "answers_id_seq")
    private Long id;

    @Column(name = "student_id")
    private String studentId;

    private String review;

    @Column(name = "lastupdatedtime")
    private Timestamp lastUpdatedTime;

    @Column(name = "createdtime")
    private Timestamp createdTime;

    @ManyToOne(fetch = FetchType.LAZY)
    private HomeworkJPA homework;

    @OneToMany(orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "relation_id")
    @Where(clause = "type = 'ANSWER'")
    private List<FileInfoJPA> files;

    @OneToOne(fetch = FetchType.LAZY)
    private GradeJPA grade;

    public Timestamp getLastUpdatedTime() {
        return lastUpdatedTime;
    }

    public Long getId() {
        return id;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getReview() {
        return review;
    }

    public Timestamp getCreatedTime() {
        return createdTime;
    }

    public HomeworkJPA getHomework() {
        return homework;
    }

    public GradeJPA getGrade() {
        return grade;
    }

    public List<FileInfoJPA> getFiles() {
        return files;
    }

    public void setLastUpdatedTime(Timestamp lastUpdatedTime) {
        this.lastUpdatedTime = lastUpdatedTime;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCreatedTime(Timestamp createdTime) {
        this.createdTime = createdTime;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public void setReview(String review) {
        this.review = review;
    }
}
