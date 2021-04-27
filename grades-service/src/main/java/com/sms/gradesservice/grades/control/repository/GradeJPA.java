package com.sms.gradesservice.grades.control.repository;

import javax.persistence.*;

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

    public GradeJPA() {

    }
    public GradeJPA(Long id, String subject, String teacherId, String studentId, Integer grade, String description, Integer weight) {
        this.id = id;
        this.subject = subject;
        this.teacherId = teacherId;
        this.studentId = studentId;
        this.grade = grade;
        this.description = description;
        this.weight = weight;
    }

    public GradeJPA(String subject, String teacherId, String studentId, Integer grade, Integer weight) {
        this.subject = subject;
        this.teacherId = teacherId;
        this.studentId = studentId;
        this.grade = grade;
        this.weight = weight;
    }

    public GradeJPA(String subject, String teacherId, String studentId, Integer grade) {
        this.subject = subject;
        this.teacherId = teacherId;
        this.studentId = studentId;
        this.grade = grade;
    }
}
