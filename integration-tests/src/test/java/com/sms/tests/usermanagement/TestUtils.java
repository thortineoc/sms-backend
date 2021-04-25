package com.sms.tests.usermanagement;

import com.sms.usermanagement.CustomAttributesDTO;
import com.sms.usermanagement.UserDTO;

import java.util.Collections;
import java.util.UUID;

public class TestUtils {

    public static final String TEST_PREFIX = "INTEGRATION_TESTS_";
    public static final String USER_MANAGEMENT = "usermanagement-service";

    public static UserDTO getTeacherWithSubjectDTO(String firstName, String subject) {
        return UserDTO.builder()
                .customAttributes(CustomAttributesDTO.builder()
                        .subjects(Collections.singletonList(subject))
                        .build())
                .role(UserDTO.Role.TEACHER)
                .userName(UUID.randomUUID().toString())
                .id(UUID.randomUUID().toString())
                .firstName(firstName)
                .lastName(UUID.randomUUID().toString())
                .pesel(UUID.randomUUID().toString())
                .build();
    }
}
