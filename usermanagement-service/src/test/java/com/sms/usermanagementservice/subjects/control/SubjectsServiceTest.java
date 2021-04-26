package com.sms.usermanagementservice.subjects.control;

import com.google.common.collect.ImmutableMap;
import com.sms.usermanagement.UserDTO;
import com.sms.usermanagementservice.subjects.control.SubjectsService;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.Collections;
import java.util.List;
import java.util.Map;

class SubjectsServiceTest {

    private static final String ROLE = "role";
    private static final String SUBJECTS = "subjects";

    private final SubjectsService service = new SubjectsService();

    @Test
    void shouldReturnTrueForTeacherWithSubject() {
        // GIVEN
        String subject1 = "Maths";
        String subject2 = "Physics";
        UserRepresentation user = getUser(UserDTO.Role.TEACHER, Lists.newArrayList(subject1, subject2));

        // WHEN
        boolean result1 = service.isTeacherAndHasSubject(user, subject1);
        boolean result2 = service.isTeacherAndHasSubject(user, subject2);

        // THEN
        Assertions.assertTrue(result1);
        Assertions.assertTrue(result2);
    }

    @Test
    void shouldReturnFalseForNonTeacherUser() {
        // GIVEN
        String subject1 = "Maths";
        String subject2 = "Physics";
        UserRepresentation user = getUser(UserDTO.Role.STUDENT, Lists.newArrayList(subject1, subject2));

        // WHEN
        boolean result1 = service.isTeacherAndHasSubject(user, subject1);
        boolean result2 = service.isTeacherAndHasSubject(user, subject2);

        // THEN
        Assertions.assertFalse(result1);
        Assertions.assertFalse(result2);
    }

    @Test
    void shouldReturnFalseForTeacherWithoutSubject() {
        // GIVEN
        String subject1 = "Maths";
        String subject2 = "Physics";
        UserRepresentation user = getUser(UserDTO.Role.TEACHER, Lists.newArrayList(subject1));

        // WHEN
        boolean result1 = service.isTeacherAndHasSubject(user, subject1);
        boolean result2 = service.isTeacherAndHasSubject(user, subject2);

        // THEN
        Assertions.assertTrue(result1);
        Assertions.assertFalse(result2);
    }

    private UserRepresentation getUser(UserDTO.Role role, List<String> subjects) {
        UserRepresentation user = new UserRepresentation();
        Map<String, List<String>> attributes = ImmutableMap.of(
                ROLE, Collections.singletonList(role.toString()),
                SUBJECTS, subjects
        );
        user.setAttributes(attributes);
        return user;
    }
}
