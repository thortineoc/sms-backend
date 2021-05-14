package com.sms.homeworkservice.file.control;

import com.sms.context.UserContext;
import com.sms.homeworkservice.homework.control.HomeworkRepository;
import com.sms.model.homework.FileDetailJPA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Scope("request")
public class FileService {


    @Autowired
    HomeworkRepository fileRepository;

    @Autowired
    HomeworkFileRespository homeworkFileRespository;

    @Autowired
    UserContext userContext;


    public FileDetailJPA getFile(Long id) {
        Optional<FileDetailJPA> result = homeworkFileRespository.findAllById(id);
        if (result.isPresent()) return result.get();
        throw new IllegalStateException("file doesnt exists");
    }


/*    public FileLinkDTO store(MultipartFile file, Long homework) throws IOException {
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        HomeworkFileDetailJPA FileDB = FileMapper.toJPA(file, fileName, homework);
        FileDetailJPA homeworkFilesJPA;
        try {
            homeworkFilesJPA = homeworkFileRespository.save(FileDB);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        return null;
    }*/
}
