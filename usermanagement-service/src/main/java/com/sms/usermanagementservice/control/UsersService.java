package com.sms.usermanagementservice.control;
import com.sms.clients.KeycloakClient;
import com.sms.clients.entity.UserSearchParams;
import com.sms.usermanagement.UserDTO;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import java.util.*;


@Component
@Scope("request")
public class UsersService {

    // @Autowired //field injection is not recommended??? --> getUsers - nullptr
    // private KeycloakClient keycloakClient;
    private final KeycloakClient keycloakClient= new KeycloakClient();



    public List<UserRepresentation>  filterUserByParameters(Map<String, String> queryParameters){
        QueryParams queryParams= new QueryParams(queryParameters);
        FilteredUsers filterUser = new FilteredUsers();

        return filterUser.filterUsersByParam(getUsers(), queryParams);
    }

    public List<UserRepresentation> getUsers() {
        List<UserRepresentation> users = keycloakClient.getAllUsers();
        if (!users.isEmpty()) {
           return users;
        } else throw new IllegalStateException("Users not found!");
    }

    public void createStudentWithParent(UserDTO user) {
        createUser(user);

        UserSearchParams params = new UserSearchParams().username(calculateUsername(user));
        UserRepresentation createdStudent = keycloakClient.getUsers(params)
                .stream().findFirst().orElseThrow(() -> new IllegalStateException("User was not created"));

        createParent(user, createdStudent);
    }

    public void createUser(UserDTO user) {
        UserRepresentation userRep = UserMapper
                .toUserRepresentation(user, calculateUsername(user), calculatePassword(user));

        if (!keycloakClient.createUser(userRep)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
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
        updateStudentRelatedUser(createdStudent, calculateUsername(user));
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
            case STUDENT: return "s_" + user.getPesel();
            case ADMIN: return "a_" + user.getPesel();
            case TEACHER: return "t_" + user.getPesel();
            case PARENT: return "p_" + user.getPesel();
            default: throw new IllegalStateException();
        }
    }

    private String calculateParentUsernameFromStudent(UserDTO user) {
        return "p_" + user.getPesel();
    }


}


