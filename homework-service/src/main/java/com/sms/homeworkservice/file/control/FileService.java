package com.sms.homeworkservice.file.control;

import com.sms.context.UserContext;
import com.sms.model.homework.FileDetailJPA;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import javax.persistence.EntityNotFoundException;
import java.util.Optional;

@Component
@Scope("request")
public class FileService {

    @Autowired
    FileRepository fileRepository;

    @Autowired
    UserContext userContext;

    public void deleteFile(Long id) {
        try {
            Optional<FileDetailJPA> file = fileRepository.findById(id);
            if (!userContext.getUserId().equals(file.get().getOwnerId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN);
            }
            fileRepository.deleteById(id);
        } catch (ConstraintViolationException e) {
            throw new IllegalArgumentException("Deleting file: " + id + " violated database constraints: " + e.getConstraintName());
        } catch (EntityNotFoundException e) {
            throw new IllegalStateException("File with ID: " + id + " does not exist, can't delete");
        }
    }
}
