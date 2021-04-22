package com.sms.usermanagementservice.entity;

import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;

@Transactional
public interface GroupDao extends CrudRepository<Group, Integer> {
    public Group findByName(String name);
}

