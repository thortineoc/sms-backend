package com.sms.usermanagementservice.users.control;

import com.sms.api.common.BadRequestException;
import com.sms.api.common.NotFoundException;
import com.sms.clients.KeycloakClient;
import com.sms.clients.entity.UserSearchParams;
import com.sms.context.UserContext;
import com.sms.api.usermanagement.CustomAttributesDTO;
import com.sms.api.usermanagement.UserDTO;
import com.sms.api.usermanagement.UsersFiltersDTO;
import com.sms.usermanagementservice.clients.GradesClient;
import com.sms.usermanagementservice.clients.HomeworksClient;
import com.sms.usermanagementservice.clients.TimetablesClient;
import com.sms.usermanagementservice.users.entity.CustomFilterParams;
import com.sms.usermanagementservice.users.entity.KeyCloakFilterParams;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Scope("request")
public class UsersService {

    @Autowired
    KeycloakClient keycloakClient;

    @Autowired
    UserFilteringService userFilteringService;

    @Autowired
    UserContext context;

    @Autowired
    GradesClient gradesClient;

    @Autowired
    HomeworksClient homeworksClient;

    @Autowired
    TimetablesClient timetablesClient;

    public List<UserDTO> filterUserByParameters(UsersFiltersDTO filterParamsDTO) {
        CustomFilterParams customFilterParams = UserMapper.mapCustomFilterParams(filterParamsDTO);
        KeyCloakFilterParams keyCloakFilterParams = UserMapper.mapKeyCloakFilterParams(filterParamsDTO);
        List<UserRepresentation> userList = userFilteringService.filterByKCParams(keyCloakFilterParams);
        return userFilteringService.customFilteringUsers(userList, customFilterParams);
    }

    public Optional<UserDTO> getUser(String id) {
        return keycloakClient.getUser(id).map(UserMapper::toDTO);
    }

    public List<UserDTO> getUsers(Set<String> ids) {
        List<UserRepresentation> allUsers = keycloakClient.getUsers(new UserSearchParams());
        return allUsers.stream()
                .filter(user -> ids.contains(user.getId()))
                .map(UserMapper::toDTO)
                .collect(Collectors.toList());
    }

    public void createStudentWithParent(UserDTO user) {
        createUser(user);

        UserSearchParams params = new UserSearchParams().username(calculateUsername(user));
        UserRepresentation createdStudent = keycloakClient.getUsers(params)
                .stream().findFirst().orElseThrow(() -> new IllegalStateException("User was not created"));

        createParent(user, createdStudent);
    }

    public void createUser(UserDTO user) {
        UserRepresentation userRep = UserMapper.toUserRepresentation(user, calculateUsername(user), calculatePassword(user));

        if (!keycloakClient.createUser(userRep)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
    }

    public void deleteUser(String userId) {
        if (context.getUserId().equals(userId)) {
            throw new BadRequestException("You can't delete yourself!");
        }
        UserRepresentation userRepresentation = keycloakClient.getUser(userId)
                .orElseThrow(() -> new NotFoundException("User with id: " + userId + " doesn't exist"));

        boolean gradesDeleted = gradesClient.deleteGrades(userId);
        boolean answersDeleted = homeworksClient.deleteAnswers(userId);
        boolean relatedUserDeleted = deleteRelatedUser(userRepresentation);
        boolean userDeleted = keycloakClient.deleteUser(userId);
        boolean filesDeleted = homeworksClient.deleteFilesByOwnerId(userId);
        boolean lessonsDeleted = true;
        if (context.getSmsRole() == UserDTO.Role.TEACHER) {
            lessonsDeleted = timetablesClient.deleteLessonsByTeacherId(userId);
        }

        if (!lessonsDeleted) throw new IllegalStateException("Couldn't delete lessons.");
        if (!filesDeleted) throw new IllegalStateException("Couldn't delete user files.");
        if (!gradesDeleted) throw new IllegalStateException("Couldn't delete user grades.");
        if (!answersDeleted) throw new IllegalStateException("Couldn't delete homework answers.");
        if (!relatedUserDeleted) throw new IllegalStateException("Couldn't delete related user.");
        if (!userDeleted) throw new IllegalStateException("Couldn't delete user.");
    }

    private void createParent(UserDTO user, UserRepresentation createdStudent) {

        UserRepresentation parent = UserMapper
                .toParentRepresentationFromStudent(user, calculateParentUsernameFromStudent(user), calculatePassword(user));
        Map<String, List<String>> parentAttributes = new HashMap<>(parent.getAttributes());
        parentAttributes.put("relatedUser", Collections.singletonList(createdStudent.getId()));
        parent.setAttributes(parentAttributes);

        if (!keycloakClient.createUser(parent)) {
            keycloakClient.deleteUser(createdStudent.getId());
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }

        updateStudentRelatedUser(createdStudent, calculateParentUsernameFromStudent(user));
    }

    private void updateStudentRelatedUser(UserRepresentation createdStudent, String parentUsername) {

        UserSearchParams params = new UserSearchParams().username(parentUsername);
        UserRepresentation createdParent = keycloakClient.getUsers(params)
                .stream().findFirst().orElseThrow(() -> new IllegalStateException("User was not created"));

        Map<String, List<String>> studentAttributes = new HashMap<>(createdStudent.getAttributes());
        studentAttributes.put("relatedUser", Collections.singletonList(createdParent.getId()));
        createdStudent.setAttributes(studentAttributes);

        if (!keycloakClient.updateUser(createdStudent.getId(), createdStudent)) {
            keycloakClient.deleteUser(createdStudent.getId());
            keycloakClient.deleteUser(createdParent.getId());
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
    }

    private String calculatePassword(UserDTO user) {
        return user.getFirstName().substring(0, Math.min(user.getFirstName().length(), 4)) +
                user.getLastName().substring(0, Math.min(user.getLastName().length(), 4));
    }

    private String calculateUsername(UserDTO user) {
        switch (user.getRole()) {
            case STUDENT:
                return "s_" + user.getPesel();
            case ADMIN:
                return "a_" + user.getPesel();
            case TEACHER:
                return "t_" + user.getPesel();
            default:
                throw new IllegalStateException();
        }
    }

    private String calculateParentUsernameFromStudent(UserDTO user) {
        return "p_" + user.getPesel();
    }

    private Boolean deleteRelatedUser(UserRepresentation userRepresentation) {
        Map<String, List<String>> userAttributes = userRepresentation.getAttributes();
        String role = userAttributes.get("role").stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("User without role"));
        if (role.equals(UserDTO.Role.PARENT.toString()) || role.equals(UserDTO.Role.STUDENT.toString())) {
            String relatedUserId = userAttributes.get("relatedUser").stream()
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("User does not have related user"));
            return keycloakClient.deleteUser(relatedUserId);
        }
        return true;
    }

    public void updateUser(UserDTO userDTO) {
        //find user
        Optional<UserRepresentation> user = keycloakClient.getUser(userDTO.getId());
        if (!user.isPresent()){
            throw new IllegalStateException("User does not exist"); //FIXME: should be NotFoundException
        }
        UserRepresentation userRep = user.get();

        //set new values
        setNewValues(userDTO, userRep);

        //save in keycloak
        if (!keycloakClient.updateUser(userRep.getId(), userRep)) {
            throw new IllegalStateException("Could not update user");
        }
    }

    private void setNewValues(UserDTO userDTO, UserRepresentation userRep) {
        userRep.setFirstName(userDTO.getFirstName());
        userRep.setLastName(userDTO.getLastName());
        userDTO.getEmail().ifPresent(userRep::setEmail);

        CustomAttributesDTO attributesDTO = userDTO.getCustomAttributes();
        attributesDTO.getPhoneNumber().ifPresent(value -> userRep.singleAttribute("phoneNumber", value));
        attributesDTO.getMiddleName().ifPresent(value -> userRep.singleAttribute("middleName", value));
        attributesDTO.getGroup().ifPresent(value -> userRep.singleAttribute("group", value));
        if (userDTO.getRole() == UserDTO.Role.TEACHER) {
            userRep.singleAttribute("subjects", String.join(",", attributesDTO.getSubjects()));
        }
    }
}
