package com.sms.homeworkservice.file.control;

import com.sms.api.homework.FileLinkDTO;
import com.sms.api.usermanagement.UserDTO;
import com.sms.context.UserContext;
import com.sms.homeworkservice.answer.control.AnswerRepository;
import com.sms.homeworkservice.homework.control.HomeworkRepository;
import com.sms.model.homework.FileJPA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Component
@Scope("request")
public class FileService {



    @Autowired
    FileRespository fileRespository;

    @Autowired
    UserContext userContext;

    @Autowired
    HomeworkRepository homeworkRepository;

    @Autowired
    AnswerRepository answerRepository;


    public FileJPA getFile(Long id) {
        Optional<FileJPA> result = fileRespository.findById(id);
        if (result.isPresent()) return result.get();
        throw new IllegalStateException("file doesnt exists");
    }


    public FileLinkDTO store(MultipartFile file, Long id, FileLinkDTO.Type type ) throws IOException {

        FileJPA FileDB = FileMapper.toJPA(file, id, type, userContext.getUserId());

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
        try {
            return FileMapper.toDTO(fileRespository.save(FileDB));
        }catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private void validateAnswer(Long id){
        if(userContext.getSmsRole() != UserDTO.Role.STUDENT) throw new IllegalStateException("Only students can add answer file");
        if(!answerRepository.existsById(id)) throw new IllegalStateException("Answer does not exists");
    }

    private void validateHomework(Long id){
        if(userContext.getSmsRole() != UserDTO.Role.TEACHER) throw new IllegalStateException("Only teacher can add homework file");
        if(!homeworkRepository.existsById(id)) throw new IllegalStateException("Homework does not exists");
    }
}
