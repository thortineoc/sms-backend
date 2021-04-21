package com.sms.usermanagementservice.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "groups" )
public class Group {

    private Integer id;
    public String name;

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Id
    public Integer getId() {
        return id;
    }

    @Column(name = "name", nullable = false)
    public String getName() {
        return this.name;
    }
}