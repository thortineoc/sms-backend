package com.sms.gradesservice.grades.control.repository;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "grades")
public class GradeJPA {

    @Id
    @Column(name = "id")
    @GeneratedValue(generator = "grades_id_seq")
    private Long id;

    @Column(name = "subject")
    private String subject;

    @Column(name = "teacher_id")
    private String teacherId;

    @Column(name = "student_id")
    private String studentId;

    @Column(name = "grade")
    private BigDecimal grade;

    @Column(name = "final")
    private Boolean isFinal;

    @Column(name = "description")
    private String description;

    @Column(name = "weight")
    private Integer weight;

    @Column(name = "createdtime", updatable = false, insertable = false)
    private Timestamp createdTime;

    @Column(name = "lastupdatedtime")
    private Timestamp lastUpdateTime;

    public void setId(Long id) {
        this.id = id;
    }

    public Timestamp getCreatedTime() {
        return createdTime;
    }

    public Timestamp getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setGrade(BigDecimal grade) {
        this.grade = grade;
    }

    public void setLastUpdateTime(Timestamp lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public void setCreatedTime(Timestamp createdTime) {
        this.createdTime = createdTime;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public void setIsFinal(Boolean isFinal) {
        this.isFinal = isFinal;
    }

    public Long getId() {
        return id;
    }

    public BigDecimal getGrade() {
        return grade;
    }

    public Integer getWeight() {
        return weight;
    }

    public String getDescription() {
        return description;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getSubject() {
        return subject;
    }

    public String getTeacherId() {
        return teacherId;
    }

    public Boolean getFinal() {
        return isFinal;
    }

    public GradeJPA() {

    }
    public GradeJPA(Long id, String subject, String teacherId, String studentId, BigDecimal grade, String description, Integer weight) {
        this.id = id;
        this.subject = subject;
        this.teacherId = teacherId;
        this.studentId = studentId;
        this.grade = grade;
        this.description = description;
        this.weight = weight;
    }

    public GradeJPA(String subject, String teacherId, String studentId, BigDecimal grade, Integer weight) {
        this.subject = subject;
        this.teacherId = teacherId;
        this.studentId = studentId;
        this.grade = grade;
        this.weight = weight;
    }

    public GradeJPA(String subject, String teacherId, String studentId, BigDecimal grade) {
        this.subject = subject;
        this.teacherId = teacherId;
        this.studentId = studentId;
        this.grade = grade;
    }
}
