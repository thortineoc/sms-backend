package com.sms.usermanagementservice.groups.control.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
public interface GroupRepository extends CrudRepository<GroupJPA, String> {

    @NonNull
    List<GroupJPA> findAllByOrderByNameAsc();
}
