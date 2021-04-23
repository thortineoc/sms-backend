package com.sms.usermanagementservice.control.subjects;

import javax.persistence.*;

@Entity
@Table(name = "subjects")
public class SubjectJPA {

    @Id
    @Column(name = "name")
    private String name;

    public SubjectJPA(String name) {
        this.name = name;
    }

    public SubjectJPA() {
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
