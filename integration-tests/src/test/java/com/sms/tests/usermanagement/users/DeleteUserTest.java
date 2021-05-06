package com.sms.tests.usermanagement.users;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.sms.clients.WebClient;
import com.sms.usermanagement.CustomAttributesDTO;
import com.sms.usermanagement.UserDTO;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static com.sms.tests.usermanagement.TestUtils.TEST_PREFIX;

public class DeleteUserTest {

    private static final String FIRST_NAME = "aaaa" + UUID.randomUUID().toString();
    private static final String LAST_NAME = "bbbb" + UUID.randomUUID().toString();
    private static final String EMAIL = FIRST_NAME + "@" + LAST_NAME + ".com";
    private static final String PASSWORD = "aaaabbbb";
    private static final String PESEL = UUID.randomUUID().toString();

    @BeforeEach
    @AfterEach
    public void cleanup() {
        Response response = UserUtils.getUsers(ImmutableMap.of("middleName", TEST_PREFIX));
        if (response.statusCode() == 200) {
            Arrays.stream(response.as(UserDTO[].class)).map(UserDTO::getId).forEach(UserUtils::deleteUser);
        }
    }

    @Test
    void shouldThrowExceptionOnInvalidId() {
        // DELETE USER
        UserUtils.deleteUser("123455").then().statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void shouldDeleteStudentWithParentUser() {
        // GIVEN
        // create new user and validate its creation
        UserDTO user = createUserDTO(UserDTO.Role.STUDENT, "5" + EMAIL);
        UserUtils.createUser(user).then().statusCode(HttpStatus.NO_CONTENT.value());

        // get user from kc with valid id
        UserDTO createdStudent = Arrays.stream(UserUtils.getUsers(ImmutableMap.of(
                "lastName", LAST_NAME,
                "role", "STUDENT"
        )).as(UserDTO[].class)).findFirst().orElseThrow(() -> new RuntimeException("Student wasn't created"));
        UserDTO createdParent = Arrays.stream(UserUtils.getUsers(ImmutableMap.of(
                "lastName", LAST_NAME,
                "role", "PARENT"
        )).as(UserDTO[].class)).findFirst().orElseThrow(() -> new RuntimeException("Parent wasn't created"));

        // DELETE STUDENT USER
        UserUtils.deleteUser(createdStudent.getId()).then().statusCode(HttpStatus.NO_CONTENT.value());

        // validate parent and student have been deleted
        Response response = UserUtils.getUsers(ImmutableMap.of("lastName", LAST_NAME));
        response.then().statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    void shouldDeleteTeacherUser() {
        // GIVEN
        // create new user and validate it's creation
        UserDTO user = createUserDTO(UserDTO.Role.TEACHER,  "4" + EMAIL);
        UserUtils.createUser(user).then().statusCode(HttpStatus.NO_CONTENT.value());

        // get user from kc with valid id
        UserDTO[] createdUsers = UserUtils.getUsers(ImmutableMap.of("lastName", LAST_NAME))
                .as(UserDTO[].class);
        Assertions.assertEquals(1, createdUsers.length);
        UserDTO createdUser = createdUsers[0];

        // DELETE USER
        UserUtils.deleteUser(createdUser.getId());

        // validate user has been deleted
        UserUtils.getUsers(ImmutableMap.of("lastName", LAST_NAME))
                .then().statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    void shouldThrowBadRequestIfAdminDeletesHimself(){
        // GIVEN
        // create new user and validate it's creation
        UserDTO user = createUserDTO(UserDTO.Role.ADMIN, "3" + EMAIL);
        UserUtils.createUser(user).then().statusCode(HttpStatus.NO_CONTENT.value());

        // get user from kc with valid id
        UserDTO[] createdUsers = UserUtils.getUsers(ImmutableMap.of("lastName", LAST_NAME))
                .as(UserDTO[].class);
        Assertions.assertEquals(1, createdUsers.length);
        UserDTO createdUser = createdUsers[0];

        // prepare second webclient
        WebClient adminWebClient = new WebClient("a_" + PESEL, PASSWORD);

        // TRY TO DELETE USER
        UserUtils.deleteUser(adminWebClient, createdUser.getId())
                .then().statusCode(HttpStatus.BAD_REQUEST.value());

        // validate user has NOT been deleted
        Response response = UserUtils.getUser(createdUser.getId());
        response.then().statusCode(HttpStatus.OK.value());
        UserDTO adminUser = response.as(UserDTO.class);

        // DELETE SECOND ADMIN USER
        UserUtils.deleteUser(createdUser.getId())
                .then().statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    void shouldThrowIfUserWithoutAdminRoleDeletesUser() {
        // GIVEN
        // create new user and validate it's creation
        UserDTO user = createUserDTO(UserDTO.Role.TEACHER, "1" + EMAIL);
        UserDTO otherUser = createUserDTO(UserDTO.Role.STUDENT, "2" + EMAIL);
        UserUtils.createUser(user);
        UserUtils.createUser(otherUser);

        // get user from kc with valid id
        List<UserDTO> createdUsers = Arrays.asList(UserUtils.getUsers(ImmutableMap
                .of("lastName", LAST_NAME)).as(UserDTO[].class));
        UserDTO createdUser = createdUsers.get(0);
        Assertions.assertEquals(3, createdUsers.size()); // +1 for parent

        // prepare second webclient
        WebClient teacherWebClient = new WebClient("t_" + PESEL, PASSWORD);
        // DELETE USER
        UserUtils.deleteUser(teacherWebClient, createdUser.getId())
                .then().statusCode(HttpStatus.FORBIDDEN.value());

        // validate user has NOT been deleted
        createdUsers = Arrays.asList(UserUtils.getUsers(ImmutableMap
                .of("lastName", LAST_NAME)).as(UserDTO[].class));
        Assertions.assertEquals(3, createdUsers.size());
    }

    UserDTO createUserDTO(UserDTO.Role role, String mail) {

        List<String> subjects = Lists.newArrayList("subject1", "subject2");
        CustomAttributesDTO attributesDTO = CustomAttributesDTO.builder()
                .phoneNumber("132-234-234")
                .middleName(TEST_PREFIX + "middleName")
                .relatedUser("example-user")
                .group("example-group")
                .subjects(subjects)
                .build();

        return UserDTO.builder()
                .id("null")
                .userName(UUID.randomUUID().toString())
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .pesel(PESEL)
                .role(role)
                .email(mail)
                .customAttributes(attributesDTO)
                .build();
    }

//    private GradeDTO buildGrade(String id){
//        return GradeDTO.builder()
//                .grade(BigDecimal.valueOf(5))
//                .description("test")
//                .isFinal(false)
//                .subject("Math")
//                .weight(1)
//                .teacherId("d2364974-cfa8-45c0-b133-57df2c89a327")
//                .studentId(id)
//                .build();
//    }

//    private void createGrade(GradeDTO gradeDTO){
//        TEACHERCLIENT.request("grades-service")
//                .contentType(MediaType.APPLICATION_JSON)
//                .body(gradeDTO)
//                .put("/grades")
//                .then()
//                .statusCode(HttpStatus.OK.value());
//    }
}
