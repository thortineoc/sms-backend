package com.sms.gradesservice.grades.control.repository;

import javax.persistence.*;

@Entity
@Table(name="grades")
public class GradeJPA {

    @Id
    @GeneratedValue(generator = "grades_id_seq")
    private Integer id;

    private String subject;

    private String teacher_id;

    private String student_id;

    private Integer grade;

    private String description;

    private Integer weight;

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTeacherId() {
        return teacher_id;
    }

    public void setTeacherId(String teacher_id) {
        this.teacher_id = teacher_id;
    }

    public String getStudentId() {
        return student_id;
    }

    public void setStudentId(String student_id) {
        this.student_id = student_id;
    }

    public Integer getGrade() {
        return grade;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }
}
