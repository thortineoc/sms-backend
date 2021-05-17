package com.sms.homeworkservice.homework.control;

import com.sms.api.common.Util;
import com.sms.api.homework.AnswerDTO;
import com.sms.api.homework.HomeworkDTO;
import com.sms.api.homework.SimpleHomeworkDTO;
import com.sms.api.usermanagement.CustomAttributesDTO;
import com.sms.api.usermanagement.UserDTO;
import com.sms.api.usermanagement.UsersFiltersDTO;
import com.sms.context.UserContext;
import com.sms.homeworkservice.answer.control.AnswerMapper;
import com.sms.homeworkservice.clients.UserManagementClient;
import com.sms.model.homework.AnswerJPA;
import com.sms.model.homework.HomeworkJPA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

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
    UserContext userContext;

    @Autowired
    UserManagementClient userManagementClient;

    public Optional<HomeworkDTO> getDetails(Long id) {
        Optional<HomeworkJPA> homework = homeworkRepository.getHomeworkDetails(id);
        List<AnswerDTO> answers = homework.map(this::addUsersToAnswers).orElse(Collections.emptyList());
        return homework.map(h -> HomeworkMapper.toDetailDTO(h, answers));
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

    private List<AnswerDTO> addUsersToAnswers(HomeworkJPA homework) {
        Map<String, UserDTO> usersInGroup = userManagementClient.getUsers(UsersFiltersDTO.builder()
                .group(homework.getGroup())
                .build()).stream().collect(Collectors.toMap(UserDTO::getId, Function.identity()));

        return homework.getAnswers().stream()
                .map(a -> AnswerMapper.toDetailDTO(a, getMatchingUser(usersInGroup, a)))
                .collect(Collectors.toList());
    }

    private UserDTO getMatchingUser(Map<String, UserDTO> users, AnswerJPA answer) {
        return Util.getOpt(users, answer.getStudentId())
                .orElseThrow(() -> new IllegalStateException("Answer " + answer.getId() + " doesn't have a student"));
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
}
