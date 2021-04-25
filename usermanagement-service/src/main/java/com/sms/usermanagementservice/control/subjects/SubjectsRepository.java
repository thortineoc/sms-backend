package com.sms.usermanagementservice.control.subjects;

import org.springframework.data.repository.CrudRepository;

import java.util.List;


public interface SubjectsRepository extends CrudRepository<SubjectJPA, String> {

    List<SubjectJPA> findAll();
}
