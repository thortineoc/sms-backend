package com.sms.tests.usermanagement;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sms.clients.KeycloakClient;
import com.sms.clients.WebClient;
import com.sms.clients.entity.UserSearchParams;
import com.sms.common.JDK8Mapper;
import com.sms.usermanagement.CustomAttributesDTO;
import com.sms.usermanagement.UserDTO;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class CreateNewUserTest {

    private final static WebClient CLIENT = new WebClient("smsadmin", "smsadmin");
    private final JDK8Mapper mapper = new JDK8Mapper();
    private final static KeycloakClient KEYCLOAK_CLIENT = new KeycloakClient();

    @Test
    void shouldReturnForbiddenWhenNotAdmin() throws JsonProcessingException {

        WebClient tempWebClient = new WebClient();

        CustomAttributesDTO attributesDTO = CustomAttributesDTO.builder().build();

        UserDTO userDTO = UserDTO.builder()
                .id("null")
                .userName("null")
                .firstName("firstName")
                .lastName("lastName")
                .pesel("pesel")
                .role(UserDTO.Role.TEACHER)
                .email("email@email.com")
                .customAttributes(attributesDTO)
                .build();

        String userJson = mapper.writeValueAsString(userDTO);

        Response response = tempWebClient.request("usermanagement-service")
                .contentType("application/json")
                .body(userJson)
                .post("/users");

        Assertions.assertEquals(HttpStatus.SC_FORBIDDEN, response.getStatusCode());
    }

    @Test
    void shouldReturnBadRequestWhenMissingBody(){
        Response response = CLIENT.request("usermanagement-service")
                .contentType("application/json")
                .post("/users");

        Assertions.assertEquals(HttpStatus.SC_BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void shouldCreateNewTeacherWithoutRedundantData() throws JsonProcessingException {

        List<String> subjects = Stream.of("subject1", "subject2").collect(Collectors.toList());

        CustomAttributesDTO attributesDTO = CustomAttributesDTO.builder()
                .phoneNumber("132-234-234")
                .middleName("middleName")
                .relatedUser("example-user")
                .group("example-group")
                .subjects(subjects)
                .build();

        UserDTO userDTO = UserDTO.builder()
                .id("null")
                .userName("null")
                .firstName("firstName")
                .lastName("lastName")
                .pesel("pesel")
                .role(UserDTO.Role.TEACHER)
                .email("mail@email.com")
                .customAttributes(attributesDTO)
                .build();

        String userJson = mapper.writeValueAsString(userDTO);

        Response response = CLIENT.request("usermanagement-service")
                .contentType("application/json")
                .body(userJson)
                .post("/users");

        Assertions.assertEquals(HttpStatus.SC_OK, response.getStatusCode());

        //TODO check details of created user with API call
        //TODO delete user with API call

        UserSearchParams params = new UserSearchParams().username("t_pesel");
        UserRepresentation createdUser = KEYCLOAK_CLIENT.getUsers(params).get(0);

        KEYCLOAK_CLIENT.deleteUser(createdUser.getId());
    }

    @Test
    void shouldCreateNewAdminWithoutRedundantData() throws JsonProcessingException {

        List<String> subjects = Stream.of("subject1", "subject2").collect(Collectors.toList());

        CustomAttributesDTO attributesDTO = CustomAttributesDTO.builder()
                .phoneNumber("132-234-234")
                .middleName("middleName")
                .relatedUser("example-user")
                .group("example-group")
                .subjects(subjects)
                .build();

        UserDTO userDTO = UserDTO.builder()
                .id("null")
                .userName("null")
                .firstName("firstName")
                .lastName("lastName")
                .pesel("pesel")
                .role(UserDTO.Role.ADMIN)
                .email("mail@email.com")
                .customAttributes(attributesDTO)
                .build();

        String userJson = mapper.writeValueAsString(userDTO);

        Response response = CLIENT.request("usermanagement-service")
                .contentType("application/json")
                .body(userJson)
                .post("/users");

        Assertions.assertEquals(HttpStatus.SC_OK, response.getStatusCode());

        //TODO check details of created user with API call
        //TODO delete user with API call

        UserSearchParams params = new UserSearchParams().username("a_pesel");
        UserRepresentation createdUser = KEYCLOAK_CLIENT.getUsers(params).get(0);

        KEYCLOAK_CLIENT.deleteUser(createdUser.getId());
    }

    @Test
    void shouldCreateNewStudentWithParent() throws JsonProcessingException {

        List<String> subjects = Stream.of("subject1", "subject2").collect(Collectors.toList());

        CustomAttributesDTO attributesDTO = CustomAttributesDTO.builder()
                .phoneNumber("132-234-234")
                .middleName("middleName")
                .relatedUser("example-user")
                .group("example-group")
                .subjects(subjects)
                .build();

        UserDTO userDTO = UserDTO.builder()
                .id("null")
                .userName("null")
                .firstName("firstName")
                .lastName("lastName")
                .pesel("pesel")
                .role(UserDTO.Role.STUDENT)
                .email("mail@email.com")
                .customAttributes(attributesDTO)
                .build();

        String userJson = mapper.writeValueAsString(userDTO);

        Response response = CLIENT.request("usermanagement-service")
                .contentType("application/json")
                .body(userJson)
                .post("/users");

        Assertions.assertEquals(HttpStatus.SC_OK, response.getStatusCode());

        //TODO check details of created users with API call
        //TODO delete user with API call

        UserSearchParams paramsStudent = new UserSearchParams().username("s_pesel");
        UserRepresentation createdStudent= KEYCLOAK_CLIENT.getUsers(paramsStudent).get(0);
        KEYCLOAK_CLIENT.deleteUser(createdStudent.getId());

        UserSearchParams paramsParent = new UserSearchParams().username("p_pesel");
        UserRepresentation createdParent= KEYCLOAK_CLIENT.getUsers(paramsParent).get(0);
        KEYCLOAK_CLIENT.deleteUser(createdParent.getId());
    }

    @Test
    void shouldReturnConflictWhenCreatingTwoUsersWithIdenticalData() throws JsonProcessingException {

        List<String> subjects = Stream.of("subject1", "subject2").collect(Collectors.toList());

        CustomAttributesDTO attributesDTO = CustomAttributesDTO.builder()
                .phoneNumber("132-234-234")
                .middleName("middleName")
                .relatedUser("example-user")
                .group("example-group")
                .subjects(subjects)
                .build();

        UserDTO userDTO = UserDTO.builder()
                .id("null")
                .userName("null")
                .firstName("firstName")
                .lastName("lastName")
                .pesel("pesel")
                .role(UserDTO.Role.ADMIN)
                .email("mail@email.com")
                .customAttributes(attributesDTO)
                .build();

        String userJson = mapper.writeValueAsString(userDTO);

        Response response = CLIENT.request("usermanagement-service")
                .contentType("application/json")
                .body(userJson)
                .post("/users");

        Assertions.assertEquals(HttpStatus.SC_OK, response.getStatusCode());

        response = CLIENT.request("usermanagement-service")
                .contentType("application/json")
                .body(userJson)
                .post("/users");

        Assertions.assertEquals(HttpStatus.SC_CONFLICT, response.getStatusCode());

        //TODO delete user with API call

        UserSearchParams params = new UserSearchParams().username("a_pesel");
        UserRepresentation createdUser= KEYCLOAK_CLIENT.getUsers(params).get(0);
        KEYCLOAK_CLIENT.deleteUser(createdUser.getId());

    }

}
