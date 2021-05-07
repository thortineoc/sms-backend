package com.sms.homeworkservice.homeworks.control;


import com.sms.context.UserContext;
import com.sms.homeworks.HomeworkDTO;
import com.sms.homeworkservice.homeworks.control.repository.FileJPA;
import com.sms.homeworkservice.homeworks.control.repository.HomeworkJPA;
import com.sms.homeworkservice.homeworks.control.repository.HomeworksFilesRepository;
import com.sms.homeworkservice.homeworks.control.repository.HomeworksRepository;
import com.sms.homeworkservice.homeworks.control.response.ResponseFile;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Scope("request")
public class HomeworksService {

    @Autowired
    UserContext userContext;

    @Autowired
    HomeworksRepository homeworksRepository;

    @Autowired
    HomeworksFilesRepository homeworksFilesRepository;

    public HomeworkDTO updateHomework(HomeworkDTO homeworkDTO) {
        HomeworkJPA homework = HomeworkMapper.toJPA(homeworkDTO);
        homework.setTeacherid(userContext.getUserId());

        try {
            HomeworkJPA updatedHomework = homeworksRepository.save(homework);
            return HomeworkMapper.toDTO(updatedHomework);
        } catch (ConstraintViolationException e) {
            throw new IllegalArgumentException("Saving grade: " + homework.getId() + " violated database constraints: " + e.getConstraintName());
        } catch (EntityNotFoundException e) {
            throw new IllegalStateException("Grade with ID: " + homework.getId() + " does not exist, can't update: " + e.getMessage());
        } catch (Exception e){
            throw new IllegalStateException(e);
        }

    }

    public ResponseFile store(MultipartFile file, Integer homework) throws IOException {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        validateFileName(fileName);
        FileJPA FileDB = new FileJPA(homework, fileName, (int) file.getSize(), file.getBytes());
        FileJPA fileJPA;
        try {
            fileJPA = homeworksFilesRepository.save(FileDB);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        return new ResponseFile(
                fileJPA.getFileName(),
                createDownloadUri(fileJPA),
                fileJPA.getFile().length,
                fileJPA.getId());
    }

    public FileJPA getFile(Long id) {
        if(homeworksFilesRepository.findById(id).isPresent())
        return homeworksFilesRepository.findById(id).get();
        else throw new IllegalStateException("doesnt exist");
    }

    public List<ResponseFile> getFilesInfo(Integer id){
        return homeworksFilesRepository.findAllByHomeworkid(id).stream().map(dbFile -> {
            String fileDownloadUri = ServletUriComponentsBuilder
                    .fromCurrentContextPath()
                    .path("/files/")
                    .path(dbFile.getFileName())
                    .toUriString();

            return new ResponseFile(
                    dbFile.getFileName(),
                    fileDownloadUri,
                    dbFile.getFile().length,
                    dbFile.getId());
        }).collect(Collectors.toList());
    }

    private void validateFileName(String filename) {
        if (homeworksFilesRepository.findAll().stream().anyMatch(c -> c.getFileName().equals(filename)))
            throw new IllegalArgumentException("file exists in database: + filename");
    }

    private String createDownloadUri(FileJPA file) {
        return ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/files/")
                .path(file.getFileName())
                .toUriString();
    }

}

