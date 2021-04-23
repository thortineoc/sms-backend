package com.sms.usermanagementservice.entity;

import javax.persistence.*;

@Entity
@Table(name = "groups")
public class Group {

    @Id
    private String name;

    public Group(String name) {
        this.name = name;
    }


    public Group() {
    }


    public String getName() {
        return this.name;
    }


}