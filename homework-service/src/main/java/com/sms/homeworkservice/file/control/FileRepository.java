package com.sms.homeworkservice.file.control;

import com.sms.model.homework.FileBaseJPA;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.CrudRepository;

public interface FileRepository extends CrudRepository<FileBaseJPA, Long> {

    /*
    @Modifying
    int deleteAllById(Long id);*/
}
