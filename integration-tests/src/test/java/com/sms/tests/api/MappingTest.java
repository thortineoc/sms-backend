package com.sms.tests.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.Lists;
import com.sms.api.common.JDK8Mapper;
import com.sms.api.usermanagement.CustomAttributesDTO;
import com.sms.api.usermanagement.UserDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;


public class MappingTest {

    private final JDK8Mapper mapper = new JDK8Mapper();

    @Test
    void shouldMapUserDtoToString() throws JsonProcessingException {

        //CREATE NEW USER
        UserDTO user = createUserDTO(UserDTO.Role.ADMIN);

        //MAP USERDTO TO STRING
        String userString = mapper.writeValueAsString(user);

        //MAP STRING TO USERDTO
        UserDTO userFromString = mapper.readValue(userString, UserDTO.class);

        //COMPARE
        Assertions.assertEquals(user, userFromString);
    }

    UserDTO createUserDTO(UserDTO.Role role){

        List subjects = Lists.newArrayList("subject1", "subject2");

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
                .role(role)
                .email("mail@email.com")
                .customAttributes(attributesDTO)
                .build();

        return userDTO;
    }
}
