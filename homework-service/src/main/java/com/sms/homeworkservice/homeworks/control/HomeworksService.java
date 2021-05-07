package com.sms.homeworkservice.homeworks.control;


import com.sms.context.UserContext;
import com.sms.homeworks.HomeworkDTO;
import com.sms.homeworkservice.homeworks.control.repository.FileJPA;
import com.sms.homeworkservice.homeworks.control.repository.HomeworkJPA;
import com.sms.homeworkservice.homeworks.control.repository.HomeworksFilesRepository;
import com.sms.homeworkservice.homeworks.control.repository.HomeworksRepository;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

@Component
@Scope("request")
public class HomeworksService {

    @Autowired
    UserContext userContext;

    @Autowired
    HomeworksRepository homeworksRepository;

    @Autowired
    HomeworksFilesRepository homeworksFilesRepository;

    public void updateHomework(HomeworkDTO homeworkDTO) {
        HomeworkJPA homework = HomeworkMapper.toJPA(homeworkDTO);
        homework.setTeacherid(userContext.getUserId());

        List<HomeworkJPA> homeworkJPAList = homeworksRepository.findAll();
        try {
            HomeworkJPA updatedHomework = homeworksRepository.save(homework);
        } catch (ConstraintViolationException e) {
            throw new IllegalArgumentException("Saving grade: " + homework.getId() + " violated database constraints: " + e.getConstraintName());
        } catch (EntityNotFoundException e) {
            throw new IllegalStateException("Grade with ID: " + homework.getId() + " does not exist, can't update: " + e.getMessage());
        } catch (Exception e){
            throw new IllegalStateException(e);
        }

    }

    public FileJPA store(MultipartFile file) throws IOException {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        FileJPA FileDB = new FileJPA(fileName, (int) file.getSize(), file.getBytes() );
        FileDB.setHomeworkid(21);

    return homeworksFilesRepository.save(FileDB);
    }

    public FileJPA getFile(Long id) {
        return homeworksFilesRepository.findById(id).get();
    }

    public Stream<FileJPA> getAllFiles() {
        return homeworksFilesRepository.findAll().stream();
    }

    public Stream<FileJPA> getFileByHomeworkID(Integer id) {
        return homeworksFilesRepository.findAllByHomeworkid(id).stream();
    }


    }

