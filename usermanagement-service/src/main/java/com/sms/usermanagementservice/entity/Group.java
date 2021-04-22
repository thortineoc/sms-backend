package com.sms.usermanagementservice.entity;

import com.sms.usermanagement.GroupDTO;

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

    public Group(Integer id){
        this.id = id;
    }

    public Group() {

    }

    public Integer getId(){
        return this.id;
    }

    public String getName(){
        return this.name;
    }

    public GroupDTO getGroupDTO(){
        return GroupDTO.builder().id(this.id).name(this.name).build();
    }


}