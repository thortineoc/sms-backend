package com.sms.homeworkservice.file.control;
import com.sms.context.UserContext;
import com.sms.model.homework.FileBaseJPA;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.naming.AuthenticationException;
import javax.persistence.EntityNotFoundException;
import java.util.NoSuchElementException;
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
            Optional<FileBaseJPA> file = fileRepository.findById(id);
            if(!userContext.getUserId().equals(file.get().getOwnerId())) {
                throw new AuthenticationException();
            }
            fileRepository.deleteById(id);
            /*
            int amount = fileRepository.deleteAllById(id);
            if (amount == 0) {
                throw new IllegalStateException("File with ID: " + id + " wasn't deleted");
            } else if (amount > 1) {
                throw new IllegalStateException("While deleting file with ID: " + id + " other files were deleted");
            }*/
        } catch (ConstraintViolationException e) {
            throw new IllegalArgumentException("Deleting file: " + id + " violated database constraints: " + e.getConstraintName());
        } catch (EntityNotFoundException e) {
            throw new IllegalStateException("File with ID: " + id + " does not exist, can't delete");
        } catch (AuthenticationException e) {
            throw new IllegalStateException("This user is not the owner of the file  with ID: " + id + " cannot delete");
        } catch (NoSuchElementException e) {
            throw new IllegalStateException("Couldn't find file with ID: " + id);
        }
    }
}
