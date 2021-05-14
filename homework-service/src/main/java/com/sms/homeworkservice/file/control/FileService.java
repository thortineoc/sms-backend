package com.sms.homeworkservice.file.control;
import com.sms.context.UserContext;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.EntityNotFoundException;

@Component
@Scope("request")
public class FileService {

    @Autowired
    FileRepository fileRepository;

    /*
    @Autowired
    UserContext userContext;
    */
    public void deleteFile(Long id) {
        try {
            //findById i sprawdzic czy sie zgadza z ownerid
            fileRepository.deleteById(id);
        } catch (ConstraintViolationException e) {
            throw new IllegalArgumentException("Deleting file: " + id + " violated database constraints: " + e.getConstraintName());
        } catch (EntityNotFoundException e) {
            throw new IllegalStateException("File with ID: " + id + " does not exist, can't delete: ");
        }
    }
}
