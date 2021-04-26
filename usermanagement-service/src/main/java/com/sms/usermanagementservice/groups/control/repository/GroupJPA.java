package com.sms.usermanagementservice.groups.control.repository;

import javax.persistence.*;

@Entity
@Table(name = "groups")
public class GroupJPA {

    @Id
    private String name;

    public GroupJPA(String name) {
        this.name = name;
    }

    public GroupJPA() { }

    public String getName() {
        return this.name;
    }

}