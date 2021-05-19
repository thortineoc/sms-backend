package com.sms.homeworkservice.homework.control;

import com.sms.api.common.Util;
import com.sms.api.homework.AnswerWithStudentDTO;
import com.sms.api.homework.FileLinkDTO;
import com.sms.api.homework.HomeworkDTO;
import com.sms.api.homework.SimpleHomeworkDTO;
import com.sms.api.usermanagement.CustomAttributesDTO;
import com.sms.api.usermanagement.UserDTO;
import com.sms.api.usermanagement.UsersFiltersDTO;
import com.sms.context.UserContext;
import com.sms.homeworkservice.answer.control.AnswerMapper;
import com.sms.homeworkservice.answer.control.AnswerRepository;
import com.sms.homeworkservice.clients.UserManagementClient;
import com.sms.homeworkservice.file.control.FileRespository;
import com.sms.model.homework.AnswerJPA;
import com.sms.model.homework.HomeworkJPA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;


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

    public Optional<SimpleHomeworkDTO> getDetails(Long id) {
        Optional<HomeworkJPA> homework = homeworkRepository.getHomeworkDetails(id);
        switch (userContext.getSmsRole()) {
            case TEACHER: return homework.map(h -> HomeworkMapper
                    .toTeacherDetailDTO(h, homework.map(this::getStudentsWithAnswers).orElse(Collections.emptyList())));
            case STUDENT:
            case PARENT: return homework.map(h -> HomeworkMapper
                    .toStudentDetailDTO(h, answerRepository.findByStudentIdAndHomeworkId(userContext.getUserId(), h.getId())));
            default: throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Incorrect role: " + userContext.getSmsRole() + " (you shouldn't be here)");
        }
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

    private List<AnswerWithStudentDTO> getStudentsWithAnswers(HomeworkJPA homework) {
        Map<String, AnswerJPA> answersByStudentIds = homework.getAnswers().stream()
                .collect(Collectors.toMap(AnswerJPA::getStudentId, Function.identity()));

        return userManagementClient.getUsers(UsersFiltersDTO.builder()
                .group(homework.getGroup())
                .build()).stream()
                .map(s -> AnswerWithStudentDTO.builder()
                        .student(s)
                        .answer(Util.getOpt(answersByStudentIds, s.getId()).map(AnswerMapper::toDetailDTO))
                        .build())
                .collect(Collectors.toList());
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
        HomeworkDTO dto = validateHomework(homeworkDTO);
        if (homeworkRepository.updateTable(
                Timestamp.valueOf(dto.getDeadline()),
                dto.getGroup(),
                dto.getSubject(),
                dto.getId().get(),
                dto.getDescription(),
                dto.getTitle(),
                dto.getToEvaluate()) != 1)
            throw new IllegalStateException("id does not exists or update failed");
        return homeworkDTO;
    }


    public void deleteHomework(Long id) {
        homeworkRepository.findById(id).ifPresent(obj -> {
            if (!obj.getTeacherId().equals(userContext.getUserId()))
                throw new IllegalStateException("You are not homework owner");
        });
        List<AnswerJPA> answerJPAList = answerRepository.findAllByHomeworkId(id);
        for (AnswerJPA jpa : answerJPAList) {
            fileRepository.deleteAllByRelationIdAndType(jpa.getId(), FileLinkDTO.Type.ANSWER.toString());
            answerRepository.deleteById(jpa.getId());
        }
        fileRepository.deleteAllByRelationIdAndType(id, FileLinkDTO.Type.HOMEWORK.toString());
        homeworkRepository.deleteById(id);
    }


    HomeworkDTO validateHomework(HomeworkDTO dto) {
        if (answerRepository.existsByHomeworkId(dto.getId().get())) {
            HomeworkJPA homeworkJPA = homeworkRepository.getById(dto.getId().get()).get();
            if (homeworkJPA.getSubject().equals(dto.getSubject()) && homeworkJPA.getGroup().equals(dto.getGroup()))
                return dto;
        }
        throw new IllegalStateException("Answers exist");
    }

}
