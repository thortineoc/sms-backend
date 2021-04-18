package com.sms.tests.usermanagement;

import com.sms.authlib.UserAuthDTO;
import com.sms.clients.WebClient;
import com.sms.usermanagement.CustomAttributesDTO;
import com.sms.usermanagement.UserDTO;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CreateNewUserTest {

    private final static WebClient CLIENT = new WebClient();

    @Test
    void shouldReturnUserContextFromAnotherService() {
        // WHEN
//        Response response = CLIENT.request("homework-service")
//                .get("test/service-client-send");
//        UserAuthDTO user = response.getBody().as(UserAuthDTO.class);
//
//        // THEN
//        Assertions.assertEquals("testbackenduser", user.getUserName());
//
        CustomAttributesDTO attributesDTO = CustomAttributesDTO.builder().build();

        UserDTO userDTO = UserDTO.builder()
                .firstName("Mateusz")
                .lastName("Mulak")
                .userName("null")
                .email("e@mail.c")
                .pesel("pesel")
                .role(UserDTO.Role.STUDENT)
                .id("null")
                .customAttributes(attributesDTO)
                .build();

        Response response = CLIENT.request("usermanagement-service").post("/users", userDTO);
    }
}
