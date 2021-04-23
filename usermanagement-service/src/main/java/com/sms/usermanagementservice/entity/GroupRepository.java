package com.sms.usermanagementservice.entity;

import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
public interface GroupRepository extends CrudRepository<Group, String> {

    @NonNull
    List<Group> findAll();
}
