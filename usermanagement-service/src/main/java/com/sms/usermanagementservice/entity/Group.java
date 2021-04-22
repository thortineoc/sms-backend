package com.sms.usermanagementservice.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "groups" )
public class Group {

    @Id
    @GeneratedValue(generator="groups_id_seq")
    private Integer id;

    @NotNull
    private String name;

    public Group(String name) {
        this.name = name;
    }

    public Group() {

    }

//    private Integer id;
//    public String name;
//
//    public void setId(Integer id) {
//        this.id = id;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    @Id
//    public Integer getId() {
//        return id;
//    }
//
//    @Column(name = "name", nullable = false)
//    public String getName() {
//        return this.name;
//    }
}