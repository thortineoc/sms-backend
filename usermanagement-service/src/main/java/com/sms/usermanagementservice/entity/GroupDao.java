package com.sms.usermanagementservice.entity;

import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
public interface GroupDao extends CrudRepository<Group, Integer> {
    List<Group> findAll();
}

