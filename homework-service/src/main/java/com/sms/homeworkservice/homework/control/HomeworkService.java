package com.sms.homeworkservice.homework.control;

import com.sms.api.common.Util;
import com.sms.api.homework.AnswerWithStudentDTO;
import com.sms.api.homework.SimpleHomeworkDTO;
import com.sms.api.usermanagement.CustomAttributesDTO;
import com.sms.api.usermanagement.UserDTO;
import com.sms.api.usermanagement.UsersFiltersDTO;
import com.sms.context.UserContext;
import com.sms.homeworkservice.answer.control.AnswerMapper;
import com.sms.homeworkservice.answer.control.AnswerRepository;
import com.sms.homeworkservice.clients.UserManagementClient;
import com.sms.homeworkservice.file.control.FileRepository;
import com.sms.model.homework.AnswerJPA;
import com.sms.model.homework.HomeworkJPA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
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
    FileRepository fileRepository;

    @Autowired
    UserManagementClient userManagementClient;

    public Optional<SimpleHomeworkDTO> getDetails(Long id) {
        Optional<HomeworkJPA> homework = homeworkRepository.getHomeworkDetails(id);
        if (userContext.getSmsRole() == UserDTO.Role.TEACHER) {
            return homework.map(h -> HomeworkMapper
                    .toTeacherDetailDTO(h, homework.map(this::getStudentsWithAnswers).orElse(Collections.emptyList())));
        } else {
            return homework.map(h -> HomeworkMapper
                    .toStudentDetailDTO(h, answerRepository.findByStudentIdAndHomeworkId(getStudentId(), h.getId())));
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

    public SimpleHomeworkDTO createHomework(SimpleHomeworkDTO homeworkDTO) {
        HomeworkJPA homework = HomeworkMapper.toJPA(homeworkDTO);
        homework.setTeacherId(userContext.getUserId());

        HomeworkJPA updatedHomework = homeworkRepository.save(homework);
        return HomeworkMapper.toDTOBuilder(updatedHomework).build();
    }

    @Transactional
    public SimpleHomeworkDTO updateHomework(SimpleHomeworkDTO homeworkDTO) {
        if (!homeworkDTO.getId().isPresent()) {
            return createHomework(homeworkDTO);
        }
        validateHomework(homeworkDTO);
        boolean updated = homeworkRepository.updateTable(
                Timestamp.valueOf(homeworkDTO.getDeadline()),
                homeworkDTO.getGroup(),
                homeworkDTO.getSubject(),
                homeworkDTO.getId().get(),
                homeworkDTO.getDescription(),
                homeworkDTO.getTitle(),
                homeworkDTO.getToEvaluate()) == 1;
        if (!updated) {
            throw new IllegalStateException("id does not exists or update failed");
        }
        return homeworkDTO;
    }

    @Transactional
    public void deleteHomework(Long id) {
        Optional<HomeworkJPA> homework = homeworkRepository.findById(id);
        if (!homework.isPresent()) {
            throw new IllegalStateException("Homework " + id + "doesn't exist");
        }

        if (UserDTO.Role.TEACHER == userContext.getSmsRole()) {
            String teacherId = userContext.getUserId();
            if (!teacherId.equals(homework.get().getTeacherId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not the owner of homework with ID: " + id);
            }
        }

        deleteAssignedFiles(id, homework.get());
        homeworkRepository.deleteById(id);
    }

    private void deleteAssignedFiles(Long id, HomeworkJPA homework) {
        List<Long> answersIds = homework.getAnswers().stream().map(AnswerJPA::getId).collect(Collectors.toList());
        fileRepository.deleteHomeworksAndAnswersFiles(answersIds, id);
        answerRepository.deleteAnswerByHomeworkId(id);
    }

    private void validateHomework(SimpleHomeworkDTO dto) {
        Long homeworkId = dto.getId().get();
        Optional<HomeworkJPA> homework = homeworkRepository.getById(homeworkId);
        if (!homework.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Homework with ID: " + homeworkId + " doesn't exist, cannot update.");
        }
        boolean groupOrSubjectChanged = !dto.getSubject().equals(homework.get().getSubject())
                || !dto.getGroup().equals(homework.get().getGroup());

        if (answerRepository.existsByHomeworkId(homeworkId) && groupOrSubjectChanged) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Answers under homework: " + dto.getId() + " already exist, cannot update group and subject.");
        }
    }

    private String getStudentId() {
        switch (userContext.getSmsRole()) {
            case STUDENT:
                return userContext.getUserId();
            case PARENT:
                return (String) Util.getOpt(userContext.getCustomAttributes(), RELATED_USER)
                        .orElseThrow(() -> new IllegalStateException("Parent has no related user"));
            default:
                throw new IllegalArgumentException("Users with role " + userContext.getSmsRole() + " have no student ID");
        }
    }
}
