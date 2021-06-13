package com.sms.usermanagementservice.users.control;

import com.sms.api.common.BadRequestException;
import com.sms.api.common.NotFoundException;
import com.sms.clients.KeycloakClient;
import com.sms.clients.entity.KcException;
import com.sms.clients.entity.KcResult;
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
        KcResult<UserRepresentation> result = keycloakClient.getUser(id);
        if (result.isOk()) {
            return result.getContent().map(UserMapper::toDTO);
        } else {
            throw new KcException(result, "Couldn't fetch user " + id + " from keycloak.");
        }
    }

    public List<UserDTO> getUsers(Set<String> ids) {
        KcResult<List<UserRepresentation>> result = keycloakClient.getUsers(new UserSearchParams());
        if (!result.isOk()) {
            throw new KcException(result, "Couldn't fetch users: " + ids + " from keycloak.");
        }
        return result.getContent().orElse(Collections.emptyList()).stream()
                .filter(user -> ids.contains(user.getId()))
                .map(UserMapper::toDTO)
                .collect(Collectors.toList());
    }

    public void createStudentWithParent(UserDTO user) {
        createUser(user);

        UserSearchParams params = new UserSearchParams().username(calculateUsername(user));
        KcResult<List<UserRepresentation>> result = keycloakClient.getUsers(params);
        if (!result.isOk()) {
            throw new KcException(result, "Couldn't fetch user from keycloak.");
        }
        UserRepresentation createdStudent = result.getContent().orElse(Collections.emptyList())
                .stream().findFirst().orElseThrow(() -> new IllegalStateException("User was not created"));

        createParent(user, createdStudent);
    }

    public void createUser(UserDTO user) {
        UserRepresentation userRep = UserMapper.toUserRepresentation(user, calculateUsername(user), calculatePassword(user));
        KcResult<List<UserRepresentation>> result = keycloakClient.getUsers(new UserSearchParams().username(userRep.getUsername()));
        if (!result.isOk()) {
            throw new KcException(result, "Couldn't fetch users from keycloak.");
        }
        boolean userExists = !result.getContent().orElse(Collections.emptyList()).isEmpty();
        if (userExists) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User: " + user.getFirstName() + " " + user.getLastName() + " already exists");
        }

        KcResult<Object> creationResult = keycloakClient.createUser(userRep);
        if (!creationResult.isOk()) {
            throw new KcException(creationResult, "Creating user " + user.getFirstName() + " " + user.getLastName() + " failed");
        }
    }

    public void deleteUser(String userId) {
        if (context.getUserId().equals(userId)) {
            throw new BadRequestException("You can't delete yourself!");
        }
        KcResult<UserRepresentation> result = keycloakClient.getUser(userId);
        if (!result.isOk()) {
            throw new KcException(result, "Couldn't fetch user with id: " + userId + " from keycloak.");
        }
        UserRepresentation userRepresentation = result.getContent()
                .orElseThrow(() -> new NotFoundException("User with id: " + userId + " doesn't exist"));

        boolean gradesDeleted = gradesClient.deleteGrades(userId);
        boolean answersDeleted = homeworksClient.deleteAnswers(userId);
        boolean relatedUserDeleted = deleteRelatedUser(userRepresentation);
        boolean filesDeleted = homeworksClient.deleteFilesByOwnerId(userId);
        boolean lessonsDeleted = true;
        if (context.getSmsRole() == UserDTO.Role.TEACHER) {
            lessonsDeleted = timetablesClient.deleteLessonsByTeacherId(userId);
        }

        keycloakClient.deleteUser(userId);
        KcResult<UserRepresentation> deleteResult = keycloakClient.getUser(userId);

        if (!deleteResult.isOk()) throw new KcException(deleteResult, "Error while fetching user from keycloak.");
        if (deleteResult.getContent().isPresent()) throw new IllegalStateException("Couldn't delete user with ID: " + userId);
        if (!lessonsDeleted) throw new IllegalStateException("Couldn't delete lessons.");
        if (!filesDeleted) throw new IllegalStateException("Couldn't delete user files.");
        if (!gradesDeleted) throw new IllegalStateException("Couldn't delete user grades.");
        if (!answersDeleted) throw new IllegalStateException("Couldn't delete homework answers.");
        if (!relatedUserDeleted) throw new IllegalStateException("Couldn't delete related user.");
    }

    private void createParent(UserDTO user, UserRepresentation createdStudent) {

        UserRepresentation parent = UserMapper
                .toParentRepresentationFromStudent(user, calculateParentUsernameFromStudent(user), calculatePassword(user));
        Map<String, List<String>> parentAttributes = new HashMap<>(parent.getAttributes());
        parentAttributes.put("relatedUser", Collections.singletonList(createdStudent.getId()));
        parent.setAttributes(parentAttributes);

        KcResult<Object> result = keycloakClient.createUser(parent);
        if (!result.isOk()) {
            keycloakClient.deleteUser(createdStudent.getId());
            throw new KcException(result, "Error creating parent user in keycloak.");
        }
        KcResult<List<UserRepresentation>> parentResult = keycloakClient.getUsers(new UserSearchParams().username(parent.getUsername()));
        if (!parentResult.isOk()) {
            keycloakClient.deleteUser(createdStudent.getId());
            throw new KcException(parentResult, "Error fetching parent user from keycloak.");
        }
        Optional<UserRepresentation> createdParent = parentResult.getContent().orElse(Collections.emptyList()).stream().findFirst();
        if (!createdParent.isPresent()) {
            keycloakClient.deleteUser(createdStudent.getId());
            throw new IllegalStateException("Creating parent user failed.");
        }

        updateStudentRelatedUser(createdStudent, calculateParentUsernameFromStudent(user));
    }

    private void updateStudentRelatedUser(UserRepresentation createdStudent, String parentUsername) {

        UserSearchParams params = new UserSearchParams().username(parentUsername);
        KcResult<List<UserRepresentation>> result = keycloakClient.getUsers(params);
        if (!result.isOk()) {
            throw new KcException(result, "Couldn't fetch users from keycloak.");
        }
        UserRepresentation createdParent = result.getContent().orElse(Collections.emptyList())
                .stream().findFirst().orElseThrow(() -> new IllegalStateException("User was not created"));

        Map<String, List<String>> studentAttributes = new HashMap<>(createdStudent.getAttributes());
        studentAttributes.put("relatedUser", Collections.singletonList(createdParent.getId()));
        createdStudent.setAttributes(studentAttributes);

        KcResult<Object> updateResult = keycloakClient.updateUser(createdStudent.getId(), createdStudent);
        if (!updateResult.isOk()) {
            keycloakClient.deleteUser(createdStudent.getId());
            keycloakClient.deleteUser(createdParent.getId());
            throw new KcException(updateResult, "Updating user: " + createdStudent.getId() + " failed.");
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
                throw new IllegalStateException("Unexpected user role: " + user.getRole() + ", available roles: " + Arrays.toString(UserDTO.Role.values()));
        }
    }

    private String calculateParentUsernameFromStudent(UserDTO user) {
        return "p_" + user.getPesel();
    }

    private Boolean deleteRelatedUser(UserRepresentation userRepresentation) {
        Map<String, List<String>> userAttributes = userRepresentation.getAttributes();
        List<String> roles = userAttributes.get("role");
        if (roles == null) {
            // We have to return true here so deleting the user doesn't get interrupted,
            // even though users with no role aren't allowed
            return true;
        }
        UserDTO.Role role = UserDTO.Role.valueOf(roles.get(0));
        if (role == UserDTO.Role.PARENT || role == UserDTO.Role.STUDENT) {
            List<String> relatedUsers = userAttributes.get("relatedUser");
            if (relatedUsers == null) {
                // We have to return true here so deleting the user doesn't get interrupted,
                // even though users with no role aren't allowed
                return true;
            }
            String relatedUserId = relatedUsers.get(0);
            keycloakClient.deleteUser(relatedUserId);

            KcResult<UserRepresentation> deleteResult = keycloakClient.getUser(relatedUserId);
            if (!deleteResult.isOk()) {
                return false;
            }
            return !deleteResult.getContent().isPresent();
        }
        return true;
    }

    public void updateUser(UserDTO userDTO) {
        //find user
        KcResult<UserRepresentation> result = keycloakClient.getUser(userDTO.getId());
        if (!result.isOk()) {
            throw new KcException(result, "Error fetching user: " + userDTO.getId() + " from keycloak.");
        }
        UserRepresentation userRep = result.getContent().orElseThrow(
                () -> new IllegalStateException("User does not exist")); //FIXME: should be NotFoundException

        //set new values
        setNewValues(userDTO, userRep);

        //save in keycloak
        KcResult<Object> updateResult = keycloakClient.updateUser(userRep.getId(), userRep);
        if (!updateResult.isOk()) {
            throw new KcException(updateResult, "Updating user: " + userRep.getId() + " failed.");
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
