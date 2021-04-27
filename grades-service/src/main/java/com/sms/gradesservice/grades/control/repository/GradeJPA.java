package com.sms.gradesservice.grades.control.repository;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "grades")
public class GradeJPA {

    @Column(name = "id")
    private Long id;

    @Column(name = "subject")
    private String subject;

    @Column(name = "teacher_id")
    private String teacherId;

    @Column(name = "student_id")
    private String studentId;

    @Column(name = "grade")
    private Integer grade;

    @Column(name = "description")
    private String description;

    @Column(name = "weight")
    private Integer weight;

    public void setId(Long id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
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

    @Id
    public Long getId() {
        return id;
    }

    public Integer getGrade() {
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
}
