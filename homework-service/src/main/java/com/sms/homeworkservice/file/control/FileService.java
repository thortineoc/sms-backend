package com.sms.homeworkservice.file.control;

import com.sms.api.homework.FileLinkDTO;
import com.sms.api.homework.HomeworkDTO;
import com.sms.context.UserContext;
import com.sms.homeworkservice.homework.control.HomeworkMapper;
import com.sms.homeworkservice.homework.control.HomeworkRepository;
import com.sms.model.homework.FileDetailJPA;
import com.sms.model.homework.FileJPA;
import com.sms.model.homework.HomeworkFileDetailJPA;
import com.sms.model.homework.HomeworkFileJPA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Scope("request")
public class FileService {


    @Autowired
    HomeworkRepository fileRepository;

    @Autowired
    HomeworkFileRespository homeworkFileRespository;

    @Autowired
    UserContext userContext;


    public HomeworkFileDetailJPA getFile(Long id) {
        Optional<HomeworkFileDetailJPA> result = homeworkFileRespository.findAllById(id);
        if(result.isPresent()) return result.get();
        throw new IllegalStateException("file doesnt exists");
    }


    public FileLinkDTO store(MultipartFile file, Long homework) throws IOException {
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        HomeworkFileDetailJPA FileDB = FileMapper.toJPA(file, fileName, homework);
        FileDetailJPA homeworkFilesJPA;
        try {
            homeworkFilesJPA = homeworkFileRespository.save(FileDB);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        return null;
    }
}
