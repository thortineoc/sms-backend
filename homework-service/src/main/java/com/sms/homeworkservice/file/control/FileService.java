package com.sms.homeworkservice.file.control;

import com.sms.api.common.BadRequestException;
import com.sms.api.common.ForbiddenException;
import com.sms.api.common.NotFoundException;
import com.sms.api.homework.FileLinkDTO;
import com.sms.api.usermanagement.UserDTO;
import com.sms.context.UserContext;
import com.sms.homeworkservice.answer.control.AnswerRepository;
import com.sms.homeworkservice.homework.control.HomeworkRepository;
import com.sms.model.homework.AnswerJPA;
import com.sms.model.homework.FileJPA;
import com.sms.model.homework.HomeworkJPA;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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

    public Optional<FileJPA> getFile(Long id) {
        return fileRepository.findById(id);
    }

    @Transactional
    public FileLinkDTO store(MultipartFile file, Long id, FileLinkDTO.Type type) throws IOException {

        FileJPA fileDB = FileMapper.toJPA(file, id, type, userContext.getUserId());

        validateFileUpload(id, type);
        return FileMapper.toDTO(fileRepository.save(fileDB));
    }

    private void validateFileUpload(Long relationId, FileLinkDTO.Type type) {
        if (type == FileLinkDTO.Type.ANSWER) {
            if (userContext.getSmsRole() != UserDTO.Role.STUDENT) {
                throw new BadRequestException("Files under answers can only be uploaded by students.");
            }
            AnswerJPA answer = answerRepository.findById(relationId).orElseThrow(
                    () -> new NotFoundException("Answer: " + relationId + " not found, cannot upload file."));
            if (!userContext.getUserId().equals(answer.getStudentId())) {
                throw new ForbiddenException("User: " + userContext.getUserId() + " does not own answer: " + relationId);
            }
        } else if (type == FileLinkDTO.Type.HOMEWORK) {
            if (userContext.getSmsRole() != UserDTO.Role.TEACHER) {
                throw new BadRequestException("Files under homework can only be uploaded by teachers.");
            }
            HomeworkJPA homework = homeworkRepository.findById(relationId).orElseThrow(
                    () -> new NotFoundException("Homework: " + relationId + " not found, cannot upload file."));
            if (!userContext.getUserId().equals(homework.getTeacherId())) {
                throw new ForbiddenException("Teacher: " + userContext.getUserId() + " does not own homework: " + relationId);
            }
        } else if (type != FileLinkDTO.Type.PROFILE) {
            throw new IllegalArgumentException("Incorrect file type: " + type);
        }
    }

    @Transactional
    public void deleteFilesByOwnerId(String id) {
        validateOwnership(id);
        fileRepository.deleteAllByOwnerId(id);
    }

    @Transactional
    public void deleteFile(Long id) {
        try {
            FileJPA file = fileRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("File: " + id + " does not exist."));
            validateOwnership(file.getOwnerId());
            fileRepository.deleteById(id);
        } catch (ConstraintViolationException e) {
            throw new IllegalArgumentException("Deleting file: " + id + " violated database constraints: " + e.getConstraintName());
        } catch (EntityNotFoundException e) {
            throw new NotFoundException("File with ID: " + id + " does not exist, can't delete");
        }
    }

    private void validateOwnership(String id) {
        if (UserDTO.Role.ADMIN != userContext.getSmsRole() && !userContext.getUserId().equals(id)) {
            throw new ForbiddenException("You are not the owner of file: " + id);
        }
    }
}
