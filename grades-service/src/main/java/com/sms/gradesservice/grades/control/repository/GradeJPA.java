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


}
