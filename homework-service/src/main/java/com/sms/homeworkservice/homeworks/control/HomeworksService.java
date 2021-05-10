package com.sms.homeworkservice.homeworks.control;


import com.sms.context.UserContext;
import com.sms.homeworks.HomeworkDTO;
import com.sms.homeworks.HomeworkFileDTO;
import com.sms.homeworkservice.homeworks.control.repository.HomeworkFilesJPA;
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
import java.util.Objects;
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
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }

    }

    public HomeworkFileDTO store(MultipartFile file, Integer homework) throws IOException {
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        validateFileName(fileName, homework);
        HomeworkFilesJPA FileDB = new HomeworkFilesJPA(homework, fileName, (int) file.getSize(), file.getBytes());
        HomeworkFilesJPA homeworkFilesJPA;
        try {
            homeworkFilesJPA = homeworksFilesRepository.save(FileDB);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        return HomeworkMapper.toFileDTO(homeworkFilesJPA);
    }


    public HomeworkFilesJPA getFile(Long id) {
        if (homeworksFilesRepository.findById(id).isPresent())
            return homeworksFilesRepository.findById(id).get();
        else throw new IllegalStateException("doesnt exist");
    }

    public List<HomeworkFileDTO> getFilesInfo(Integer id) {
        List<HomeworkFileDTO> files =  homeworksFilesRepository.findAllByHomeworkid(id).stream().map(HomeworkMapper::toFileDTO).collect(Collectors.toList());
        if(files.isEmpty()) throw new IllegalStateException("Homework does not exist");
        return files;
    }

    private void validateFileName(String filename, Integer homeworkID) {
        if (homeworksFilesRepository.findAllByHomeworkid(homeworkID).stream().anyMatch(c -> c.getFileName().equals(filename)))
            throw new IllegalArgumentException("file exists in database: + filename");
    }


}

