package com.sms.tests.usermanagement.users;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.sms.clients.WebClient;
import com.sms.api.usermanagement.CustomAttributesDTO;
import com.sms.api.usermanagement.UserDTO;
import org.junit.jupiter.api.*;
import org.springframework.http.HttpStatus;

import javax.ws.rs.core.MediaType;

import java.util.UUID;

import static com.sms.tests.usermanagement.TestUtils.TEST_PREFIX;

class CreateNewUserTest {

    private final static WebClient CLIENT = new WebClient("smsadmin", "smsadmin");
    private static final String FIRST_NAME = UUID.randomUUID().toString();
    private static final String LAST_NAME = UUID.randomUUID().toString();
    private static final String PESEL = UUID.randomUUID().toString();

    @BeforeAll
    @AfterAll
    static void cleanup() {
        UserUtils.deleteTestUsers();
    }

    @Test
    void shouldReturnForbiddenWhenNotAdmin() {

        // GIVEN
        WebClient tempWebClient = new WebClient();

        UserDTO user = createUserDTO(UserDTO.Role.TEACHER);

        // SHOULD RETURN FORBIDDEN WHEN USER IS NOT AN ADMIN
        UserUtils.createUser(tempWebClient, user).then().statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void shouldReturnBadRequestWhenMissingBody() {

        // SHOULD RETURN BAD_REQUEST WHEN BODY IS MISSING
        CLIENT.request("usermanagement-service")
                .contentType(MediaType.APPLICATION_JSON)
                .post("/users")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void shouldCreateNewTeacher() {

        //GIVEN
        UserDTO user = createUserDTO(UserDTO.Role.TEACHER);

        //CREATE NEW TEACHER
        UserUtils.createUser(user).then().statusCode(HttpStatus.NO_CONTENT.value());

        //CHECK USER
        UserDTO createdUser = getOneByFirstName(FIRST_NAME);
        UserUtils.assertTeachersAreEqual(user, createdUser);

        //DELETE CREATED TEACHER
        UserUtils.deleteUser(createdUser.getId()).then().statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    void shouldCreateNewAdmin() {

        //GIVEN
        UserDTO user = createUserDTO(UserDTO.Role.ADMIN);

        //CREATE NEW ADMIN
        UserUtils.createUser(user).then().statusCode(HttpStatus.NO_CONTENT.value());

        //CHECK USER
        UserDTO createdUser = getOneByFirstName(FIRST_NAME);
        UserUtils.assertAdminsAreEqual(user, createdUser);

        //DELETE CREATED ADMIN
        UserUtils.deleteUser(createdUser.getId()).then().statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    void shouldCreateNewStudentWithParent() {

        //GIVEN
        UserDTO user = createUserDTO(UserDTO.Role.STUDENT);

        //CREATE STUDENT WITH PARENT
        UserUtils.createUser(user).then().statusCode(HttpStatus.NO_CONTENT.value());

        //CHECK USER
        UserDTO createdUser = getOneByFirstName(FIRST_NAME);
        UserUtils.assertStudentsAreEqual(user, createdUser);

        //DELETE CREATED PARENT AND STUDENT (parent gets deleted automatically)
        UserUtils.deleteUser(createdUser.getId()).then().statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    void shouldReturnConflictWhenCreatingTwoUsersWithIdenticalData() {

        //GIVEN
        UserDTO user = createUserDTO(UserDTO.Role.ADMIN);

        //CREATING FIRST USER
        UserUtils.createUser(user).then().statusCode(HttpStatus.NO_CONTENT.value());

        //SHOULD RETURN CONFLICT WHEN CREATE SECOND USER
        UserUtils.createUser(user).then().statusCode(HttpStatus.CONFLICT.value());

        // GET FIRST USER
        UserDTO createdUser = getOneByFirstName(FIRST_NAME);

        // DELETE
        UserUtils.deleteUser(createdUser.getId()).then().statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    void shouldNotCreateParentWhenCannotCreateStudent() {

        //GIVEN
        UserDTO user = createUserDTO(UserDTO.Role.ADMIN);
        UserDTO student = createUserDTO(UserDTO.Role.STUDENT);

        //CREATING FIRST USER
        UserUtils.createUser(user).then().statusCode(HttpStatus.NO_CONTENT.value());

        //SHOULD RETURN CONFLICT WHEN CREATE SECOND USER
        UserUtils.createUser(student).then().statusCode(HttpStatus.CONFLICT.value());

        //MAKE SURE THERE IS ONLY ONE USER WITH NAME lastName
        UserDTO createdUser = getOneByLastName(LAST_NAME);

        //DELETE CREATED USER
        UserUtils.deleteUser(createdUser.getId()).then().statusCode(HttpStatus.NO_CONTENT.value());
    }

    private UserDTO getOneByFirstName(String firstName) {
        UserDTO[] createdUsers = UserUtils.getUsers(ImmutableMap.of("firstName", firstName))
                .as(UserDTO[].class);
        Assertions.assertEquals(1, createdUsers.length);
        return createdUsers[0];
    }

    private UserDTO getOneByLastName(String lastName) {
        UserDTO[] createdUsers = UserUtils.getUsers(ImmutableMap.of("lastName", lastName))
                .as(UserDTO[].class);
        Assertions.assertEquals(1, createdUsers.length);
        return createdUsers[0];
    }

    private UserDTO createUserDTO(UserDTO.Role role) {
        return UserDTO.builder()
                .id("null")
                .userName("null")
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .pesel(PESEL)
                .role(role)
                .email(FIRST_NAME + "@" + LAST_NAME)
                .customAttributes(CustomAttributesDTO.builder()
                        .phoneNumber("132-234-234")
                        .middleName(TEST_PREFIX + UUID.randomUUID().toString())
                        .group("example-group")
                        .subjects(Lists.newArrayList("subject1", "subject2"))
                        .build())
                .build();
    }
}
