package com.sms.homeworkservice.homework.control;

import com.sms.api.homework.FileLinkDTO;
import com.sms.api.homework.HomeworkDTO;
import com.sms.api.homework.SimpleHomeworkDTO;
import com.sms.api.usermanagement.CustomAttributesDTO;
import com.sms.api.usermanagement.UserDTO;
import com.sms.context.UserContext;
import com.sms.homeworkservice.answer.control.AnswerRepository;
import com.sms.homeworkservice.clients.UserManagementClient;
import com.sms.homeworkservice.file.control.FileRespository;
import com.sms.model.homework.AnswerJPA;
import com.sms.model.homework.FileJPA;
import com.sms.model.homework.HomeworkJPA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.ws.rs.BadRequestException;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Component
@Scope("request")
public class HomeworkService {

    private static final String RELATED_USER = "relatedUser";
    private static final String GROUP = "group";

    @Autowired
    HomeworkRepository homeworkRepository;

    @Autowired
    AnswerRepository answerRepository;

    @Autowired
    UserContext userContext;

    @Autowired
    FileRespository fileRepository;

    @Autowired
    UserManagementClient userManagementClient;

    public Optional<HomeworkDTO> getDetails(Long id) {
        return homeworkRepository.getHomeworkDetails(id).map(HomeworkMapper::toDetailDTO);
    }

    public Map<String, Map<String, List<SimpleHomeworkDTO>>> getListForTeacher() {
        List<HomeworkJPA> result = homeworkRepository.getAllByTeacherId(userContext.getUserId());
        return HomeworkMapper.toTreeDTO(result);
    }

    public Map<String, List<SimpleHomeworkDTO>> getListForStudentAndParent() {
        return getGroup().map(homeworkRepository::getAllByGroup)
                .map(HomeworkMapper::toDTOsBySubject)
                .orElse(Collections.emptyMap());
    }

    private Optional<String> getGroup() {
        if (UserDTO.Role.STUDENT.equals(userContext.getSmsRole())) {
            return Optional.ofNullable((String) userContext.getCustomAttributes().get(GROUP));
        } else {
            String relatedUser = (String) Optional.ofNullable(userContext.getCustomAttributes().get(RELATED_USER))
                    .orElseThrow(() -> new IllegalStateException("Parent not related to any student"));

            return userManagementClient.getUser(relatedUser)
                    .map(UserDTO::getCustomAttributes)
                    .flatMap(CustomAttributesDTO::getGroup);
        }
    }

    public HomeworkDTO createHomework(HomeworkDTO homeworkDTO) {
        HomeworkJPA homework = HomeworkMapper.toJPA(homeworkDTO);
        homework.setTeacherId(userContext.getUserId());

        HomeworkJPA updatedHomework = homeworkRepository.save(homework);
        return HomeworkMapper.toDTOBuilder(updatedHomework).build();
    }

    public HomeworkDTO updateHomework(HomeworkDTO homeworkDTO) {
        if (!homeworkDTO.getId().isPresent()) return createHomework(homeworkDTO);
        Boolean existence = ifExists(homeworkDTO);
        if (homeworkRepository.updateTable(
                Timestamp.valueOf(homeworkDTO.getDeadline()),
                validateGroup(homeworkDTO, existence),
                validateSubject(homeworkDTO, existence),
                homeworkDTO.getId().get(),
                homeworkDTO.getDescription(),
                homeworkDTO.getTitle(),
                homeworkDTO.getToEvaluate()) != 1)
            throw new IllegalStateException("id does not exists or update failed");
        return homeworkDTO;
    }


    public void deleteHomework(Long id) {
        homeworkRepository.findById(id).ifPresent(obj -> {
            if (!obj.getTeacherId().equals(userContext.getUserId()))
                throw new IllegalStateException("You are not homework owner");
        });
        List<AnswerJPA> answerJPAList = answerRepository.findAllByHomeworkId(id); //wszystkie odpowiedzi
        for (AnswerJPA jpa : answerJPAList) {
            List<FileJPA> a = fileRepository.findAllByRelationIdAndType(jpa.getId(), FileLinkDTO.Type.ANSWER);
        fileRepository.deleteByRelationIdAndType(jpa.getId(), FileLinkDTO.Type.ANSWER);
        answerRepository.deleteById(jpa.getId());
        }
        fileRepository.deleteByRelationIdAndType(id, FileLinkDTO.Type.HOMEWORK); //wszystkie pliki do homeoworkd
        homeworkRepository.deleteById(id);
    }

    private String validateGroup(HomeworkDTO homeworkDTO, Boolean ifExists) {
        if (ifExists) {
            HomeworkJPA homeworkJPA = homeworkRepository.getById(homeworkDTO.getId().get()).get();
            if (homeworkJPA.getGroup().equals(homeworkDTO.getGroup())) return homeworkDTO.getGroup();
            else throw new BadRequestException("Cannot update group");
        }
        return homeworkDTO.getGroup();
    }

    private String validateSubject(HomeworkDTO homeworkDTO, Boolean ifExists) {
        if (ifExists) {
            HomeworkJPA homeworkJPA = homeworkRepository.getById(homeworkDTO.getId().get()).get();
            if (homeworkJPA.getSubject().equals(homeworkDTO.getSubject())) return homeworkDTO.getSubject();
            else throw new BadRequestException("Cannot update subject");
        }
        return homeworkDTO.getSubject();
    }

    private Boolean ifExists(HomeworkDTO homeworkDTO) {
        return answerRepository.existsByHomeworkId(homeworkDTO.getId().get());
    }

}
