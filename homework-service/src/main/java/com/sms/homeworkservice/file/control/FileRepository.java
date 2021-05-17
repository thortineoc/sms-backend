package com.sms.homeworkservice.file.control;

import com.sms.model.homework.FileDetailJPA;
import org.springframework.data.repository.CrudRepository;

public interface FileRepository extends CrudRepository<FileDetailJPA, Long> {

}
