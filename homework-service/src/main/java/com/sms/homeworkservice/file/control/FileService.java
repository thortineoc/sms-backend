package com.sms.homeworkservice.file.control;

import com.sms.api.homework.FileLinkDTO;
import com.sms.api.usermanagement.UserDTO;
import com.sms.context.UserContext;
import com.sms.homeworkservice.answer.control.AnswerRepository;
import com.sms.homeworkservice.homework.control.HomeworkRepository;
import com.sms.model.homework.FileJPA;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.util.Optional;

@Component
@Scope("request")
public class FileService {

    @Autowired
    FileRepository fileRepository;

    @Autowired
    UserContext userContext;

    @Autowired
    HomeworkRepository homeworkRepository;

    @Autowired
    AnswerRepository answerRepository;

    public FileJPA getFile(Long id) {
        return fileRepository.findById(id).orElseThrow(() -> new IllegalStateException("File " + id + " doesn't exist"));
    }

    public FileLinkDTO store(MultipartFile file, Long id, FileLinkDTO.Type type) throws IOException {

        FileJPA fileDB = FileMapper.toJPA(file, id, type, userContext.getUserId());

        switch (type){
            case ANSWER:
                validateAnswer(id);
                break;
            case HOMEWORK:
                validateHomework(id);
                break;
            default:
                throw new IllegalStateException("incorrect TYPE");
        }
        return FileMapper.toDTO(fileRepository.save(fileDB));
    }

    private void validateAnswer(Long id){
        if(userContext.getSmsRole() != UserDTO.Role.STUDENT) throw new IllegalStateException("Only students can add answer file");
        if(!answerRepository.existsById(id)) throw new IllegalStateException("Answer does not exists");
    }

    private void validateHomework(Long id){
        if(userContext.getSmsRole() != UserDTO.Role.TEACHER) throw new IllegalStateException("Only teacher can add homework file");
        if(!homeworkRepository.existsById(id)) throw new IllegalStateException("Homework does not exists");
    }

    public void deleteFile(Long id) {
        try {
            Optional<FileJPA> file = fileRepository.findById(id);
            if (file.isPresent()) {
                if (!userContext.getUserId().equals(file.get().getOwnerId())) {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN);
                }
                fileRepository.deleteById(id);
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            }
        } catch (ConstraintViolationException e) {
            throw new IllegalArgumentException("Deleting file: " + id + " violated database constraints: " + e.getConstraintName());
        } catch (EntityNotFoundException e) {
            throw new IllegalStateException("File with ID: " + id + " does not exist, can't delete");
        }
    }
}
