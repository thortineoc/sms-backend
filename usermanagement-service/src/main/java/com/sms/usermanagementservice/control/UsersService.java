package com.sms.usermanagementservice.control;

import com.sms.clients.KeycloakClient;
import com.sms.clients.entity.UserSearchParams;
import com.sms.usermanagement.UserDTO;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.HashMap;   // ← nieużywana hashmapa?
import java.util.List;
import java.util.Map;

@Component
@Scope("request")
public class UsersService {

    @Autowired
    private KeycloakClient keycloakClient;
    // ja bym ogólnie zrobił żeby to void zwracało i tylko rzucał wyjątek jak coś nie pójdzie
    public boolean createStudentWithParent(UserDTO user) {
        // te calculate bym wrzucił do UserMapper.toUserRepresentation(user, calculateUsername(), calculatePassword())
        // bo są dość proste
        String password = calculatePassword(user);
        String studentUsername = calculateStudentUsername(user);

        UserRepresentation student = UserMapper.toUserRepresentation(user, studentUsername, password);

        if(!keycloakClient.createUser(student)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        UserSearchParams params = new UserSearchParams().username(studentUsername);
        UserRepresentation createdStudent = keycloakClient.getUsers(params)
                .stream().findFirst().orElseThrow(() -> new IllegalStateException("User was not created"));
        //↓ tutaj jak naciśniesz ctrl alt L to ci zrobi spację
        if(!createParent(user, createdStudent)){
            keycloakClient.deleteUser(createdStudent.getId());
            return false;
        }

        return true;
    }

    public boolean createAdmin(UserDTO user) {
        // wrzuciłbym wywołania tych metod do username i password prosto do .toUserRepresentation chyba, nie może się
        // w nich nic zepsuć i będzie czytelniej tak chyba, 5 czy 6 linijek zostanie no nie
        String password = calculatePassword(user);
        String adminUsername = calculateAdminUsername(user);

        UserRepresentation admin = UserMapper.toUserRepresentation(user, adminUsername, password);

        if(!keycloakClient.createUser(admin)){
            // tu też można rzucać wyjątek od razu a nie zwracać false, zwłaszcza że w metodzie wyżej jest create user
            // też i jest rzucany wyjątek
            return false;
        }

        return true;
    }

    public boolean createTeacher(UserDTO user) {
        // to co wyżej
        String password = calculatePassword(user);
        String teacherUsername = calculateTeacherUsername(user);

        UserRepresentation teacher = UserMapper.toUserRepresentation(user, teacherUsername, password);

        if(!keycloakClient.createUser(teacher)){
            return false;
        }

        return true;
    }

    private String calculatePassword(UserDTO user) {
        return user.getFirstName().substring(0, Math.min(user.getFirstName().length(), 4)) +
                user.getLastName().substring(0, Math.min(user.getLastName().length(), 4));
    }
    // to tutaj mi trochę przeszkadza, może wrzuć pesel do UserDTO a nie CustomAttributesDTO, będzie wtedy user.getPesel() ładnie
    private String calculateStudentUsername(UserDTO user) {
        return "s_" + user.getCustomAttributes().getPesel();
    }

    private String calculateAdminUsername(UserDTO user) {
        return "a_" + user.getCustomAttributes().getPesel();
    }

    private String calculateTeacherUsername(UserDTO user) {
        return "t_" + user.getCustomAttributes().getPesel();
    }

    private String calculateParentUsername(UserDTO user) {
        return "p_" + user.getCustomAttributes().getPesel();
    }

    private boolean createParent(UserDTO user, UserRepresentation createdStudent){
        // to co wyżej do wywołania mappera wrzuć może bo to proste metodki są
        String parentUsername = calculateParentUsername(user);
        String password = calculatePassword(user);

        UserRepresentation parent = UserMapper.toParentRepresentationFromStudent(user, parentUsername, password);

        Map<String, List<String>> parentAttributes = parent.getAttributes();
        parentAttributes.put("relatedUser", Collections.singletonList(createdStudent.getId()));
        parent.setAttributes(parentAttributes);

        if(!keycloakClient.createUser(parent)){
            return false;
        }

        if(!updateStudentRelatedUser(createdStudent, parentUsername)){
            return false;
        }

        return true;
    }

    private boolean updateStudentRelatedUser(UserRepresentation createdStudent, String parentUsername){

        UserSearchParams params = new UserSearchParams().username(parentUsername);
        UserRepresentation createdParent = keycloakClient.getUsers(params)
                .stream().findFirst().orElseThrow(() -> new IllegalStateException("User was not created"));
        // tu nie ufam tej mapie z getAttributes, weź zrób
        // Map<String, List<String>> studentAttributes = new HashMap<>(createdStudent.getAttributes())
        Map<String, List<String>> studentAttributes = createdStudent.getAttributes();
        studentAttributes.put("relatedUser", Collections.singletonList(createdParent.getId()));
        createdStudent.setAttributes(studentAttributes);

        if(!keycloakClient.updateUser(createdStudent.getId(), createdStudent)){
            // czyli jak się nie uda update to nie usuwamy też tego studenta? czemu?
            // coś z nim jest nie tak jak się update nie udał chyba
            keycloakClient.deleteUser(createdParent.getId());
            return false;
        }

        return true;
    }
}
