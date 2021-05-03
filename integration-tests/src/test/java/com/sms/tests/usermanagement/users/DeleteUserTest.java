package com.sms.tests.usermanagement.users;

import com.google.common.collect.Lists;
import com.sms.clients.KeycloakClient;
import com.sms.clients.WebClient;
import com.sms.clients.entity.UserSearchParams;
import com.sms.grades.GradeDTO;
import com.sms.usermanagement.CustomAttributesDTO;
import com.sms.usermanagement.UserDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.HttpStatus;

import javax.ws.rs.core.MediaType;
import java.math.BigDecimal;
import java.util.List;

import static com.sms.tests.usermanagement.TestUtils.TEST_PREFIX;

public class DeleteUserTest {

    private final static WebClient CLIENT = new WebClient("smsadmin", "smsadmin");
    private final static KeycloakClient KEYCLOAK_CLIENT = new KeycloakClient();
    private final static WebClient TEACHERCLIENT = new WebClient("T_82734927389", "teacher");

    @BeforeEach
    @AfterEach
    public void cleanup() {
        UserSearchParams params = new UserSearchParams().lastName(TEST_PREFIX + "lastName");
        List<UserRepresentation> createdUsers = KEYCLOAK_CLIENT.getUsers(params);

        createdUsers.stream().map(UserRepresentation::getId).forEach(KEYCLOAK_CLIENT::deleteUser);
    }

    @Test
    void shouldThrowExceptionOnInvalidId() {
        // DELETE USER
        CLIENT.request("usermanagement-service")
                .contentType(MediaType.APPLICATION_JSON)
                .delete("/users/123123")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void shouldDeleteStudentWithParentUser() {
        // GIVEN
        // create new user and validate it's creation
        UserDTO user = createUserDTO(UserDTO.Role.STUDENT, "userName", "mail@email.com");
        createNewUser(user);
        // get user from kc with valid id
        UserSearchParams params = new UserSearchParams().lastName(TEST_PREFIX + "lastName");
        UserRepresentation createdUser = KEYCLOAK_CLIENT.getUsers(params).get(0);
        List<UserRepresentation> createdUsers = KEYCLOAK_CLIENT.getUsers(params);
        Assertions.assertEquals(2, createdUsers.size());

        //ADD GRADES
        GradeDTO gradeDTO = buildGrade(createdUser.getId());
        createGrade(gradeDTO);

        // DELETE USER
        CLIENT.request("usermanagement-service")
                .contentType(MediaType.APPLICATION_JSON)
                .delete("/users/" + createdUser.getId())
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        // validate parent and student have been deleted
        createdUsers = KEYCLOAK_CLIENT.getUsers(params);
        Assertions.assertEquals(0, createdUsers.size());
    }

    @Test
    void shouldDeleteTeacherUser() {
        // GIVEN
        // create new user and validate it's creation
        UserDTO user = createUserDTO(UserDTO.Role.TEACHER, "userName", "mail@email.com");
        createNewUser(user);
        // get user from kc with valid id
        UserSearchParams params = new UserSearchParams().lastName(TEST_PREFIX + "lastName");
        UserRepresentation createdUser = KEYCLOAK_CLIENT.getUsers(params).get(0);
        List<UserRepresentation> createdUsers = KEYCLOAK_CLIENT.getUsers(params);
        Assertions.assertEquals(1, createdUsers.size());

        // DELETE USER
        CLIENT.request("usermanagement-service")
                .contentType(MediaType.APPLICATION_JSON)
                .delete("/users/" + createdUser.getId())
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        // validate user has been deleted
        createdUsers = KEYCLOAK_CLIENT.getUsers(params);
        Assertions.assertEquals(0, createdUsers.size());
    }

    @Test
    void shouldThrowBadRequestIfAdminDeletesHimself(){
        // GIVEN
        // create new user and validate it's creation
        UserDTO user = createUserDTO(UserDTO.Role.ADMIN, "userName", "mail@email.com");
        createNewUser(user);
        // get user from kc with valid id
        UserSearchParams params = new UserSearchParams().lastName(TEST_PREFIX + "lastName");
        UserRepresentation createdUser = KEYCLOAK_CLIENT.getUsers(params).get(0);
        List<UserRepresentation> createdUsers = KEYCLOAK_CLIENT.getUsers(params);
        Assertions.assertEquals(1, createdUsers.size());

        // prepare second webclient
        WebClient adminWebClient = new WebClient("a_pesel", "firsINTE");
        // DELETE USER
        adminWebClient.request("usermanagement-service")
                .contentType(MediaType.APPLICATION_JSON)
                .delete("/users/" + createdUser.getId())
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());

        // validate user has NOT been deleted
        createdUsers = KEYCLOAK_CLIENT.getUsers(params);
        Assertions.assertEquals(1, createdUsers.size());
    }

    @Test
    void shouldThrowIfUserWithoutAdminRoleDeletesUser() {
        // GIVEN
        // create new user and validate it's creation
        UserDTO user = createUserDTO(UserDTO.Role.TEACHER, "userName", "mail@email.com");
        createNewUser(user);
        UserDTO otherUser = createUserDTO(UserDTO.Role.STUDENT, "userName1", "mail1@email.com");
        createNewUser(otherUser);
        // get user from kc with valid id
        UserSearchParams params = new UserSearchParams().lastName(TEST_PREFIX + "lastName");
        UserRepresentation createdUser = KEYCLOAK_CLIENT.getUsers(params).get(0);
        List<UserRepresentation> createdUsers = KEYCLOAK_CLIENT.getUsers(params);
        Assertions.assertEquals(3, createdUsers.size()); // +1 for parent

        // prepare second webclient
        WebClient teacherWebClient = new WebClient("t_pesel", "firsINTE");
        // DELETE USER
        teacherWebClient.request("usermanagement-service")
                .contentType(MediaType.APPLICATION_JSON)
                .delete("/users/" + createdUser.getId())
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value());

        // validate user has NOT been deleted
        createdUsers = KEYCLOAK_CLIENT.getUsers(params);
        Assertions.assertEquals(3, createdUsers.size());
    }

    void createNewUser(UserDTO user) {
        CLIENT.request("usermanagement-service")
                .contentType(MediaType.APPLICATION_JSON)
                .body(user)
                .post("/users")
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    UserDTO createUserDTO(UserDTO.Role role, String userName, String mail) {

        List<String> subjects = Lists.newArrayList("subject1", "subject2");
        CustomAttributesDTO attributesDTO = CustomAttributesDTO.builder()
                .phoneNumber("132-234-234")
                .middleName("middleName")
                .relatedUser("example-user")
                .group("example-group")
                .subjects(subjects)
                .build();

        return UserDTO.builder()
                .id("null")
                .userName(userName)
                .firstName("firstName")
                .lastName(TEST_PREFIX + "lastName")
                .pesel("pesel")
                .role(role)
                .email(mail)
                .customAttributes(attributesDTO)
                .build();
    }

    private GradeDTO buildGrade(String id){
        return GradeDTO.builder()
                .grade(BigDecimal.valueOf(5))
                .description("test")
                .isFinal(false)
                .subject("Math")
                .weight(1)
                .teacherId("d2364974-cfa8-45c0-b133-57df2c89a327")
                .studentId(id)
                .build();
    }

    private void createGrade(GradeDTO gradeDTO){
        TEACHERCLIENT.request("grades-service")
                .contentType(MediaType.APPLICATION_JSON)
                .body(gradeDTO)
                .put("/grades")
                .then()
                .statusCode(HttpStatus.OK.value());
    }

}
