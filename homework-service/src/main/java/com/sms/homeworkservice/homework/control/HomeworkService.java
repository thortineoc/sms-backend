package com.sms.homeworkservice.homework.control;

import com.sms.api.homework.HomeworkDTO;
import com.sms.api.homework.SimpleHomeworkDTO;
import com.sms.api.usermanagement.CustomAttributesDTO;
import com.sms.api.usermanagement.UserDTO;
import com.sms.context.UserContext;
import com.sms.homeworkservice.clients.UserManagementClient;
import com.sms.model.homework.HomeworkJPA;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.EntityNotFoundException;
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
    UserContext userContext;

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

    public SimpleHomeworkDTO createHomework(SimpleHomeworkDTO homeworkDTO) {
        HomeworkJPA homework = HomeworkMapper.toJPA(homeworkDTO);
        homework.setTeacherId(userContext.getUserId());

        try {
            HomeworkJPA updatedHomework = homeworkRepository.save(homework);
            return HomeworkMapper.toSimpleDTO(updatedHomework);
        } catch (ConstraintViolationException e) {
            throw new IllegalArgumentException("Saving grade: " + homework.getId() + " violated database constraints: " + e.getConstraintName());
        } catch (EntityNotFoundException e) {
            throw new IllegalStateException("Grade with ID: " + homework.getId() + " does not exist, can't update: " + e.getMessage());
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }

    }

    public void updateHomework(SimpleHomeworkDTO homeworkDTO) {
        homeworkRepository.UpdateHomework(
                Timestamp.valueOf(homeworkDTO.getDeadline()),
                homeworkDTO.getGroup(),
                homeworkDTO.getSubject(),
                homeworkDTO.getId().get(),
                homeworkDTO.getDescription(),
                homeworkDTO.getTitle(),
                homeworkDTO.getToEvaluate());
    }


}
