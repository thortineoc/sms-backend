package com.sms.timetableservice.timetables.control;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multiset;
import com.google.common.collect.Sets;
import com.sms.api.common.BadRequestException;
import com.sms.api.usermanagement.CustomAttributesDTO;
import com.sms.api.usermanagement.UserDTO;
import com.sms.timetableservice.timetables.entity.TeacherWithSubject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class TimetableGenerationServiceTest {

    TimetableGenerationService service = new TimetableGenerationService();

    @Test
    void shouldNotThrowExceptionsForValidSubjects() {
        // GIVEN
        Map<String, Map<String, Integer>> info = ImmutableMap.of(
                "teacher_1", ImmutableMap.of(
                        "Math", 4,
                        "Physics", 3),
                "teacher_2", ImmutableMap.of(
                        "Biology", 3,
                        "PE", 1));
        Map<String, UserDTO> teachers = ImmutableMap.of(
                "teacher_1", getUserDTO("teacher_1", "Math", "Physics"),
                "teacher_2", getUserDTO("teacher_2", "Biology", "PE"));
        Set<String> realSubjects = Sets.newHashSet("Math", "Physics", "Biology", "PE");

        // THEN
        Assertions.assertDoesNotThrow(() -> service.validateSubjects(realSubjects, teachers, info));
    }

    @Test
    void shouldThrowExceptionOnMissingSubjects() {
        // GIVEN
        Map<String, Map<String, Integer>> info = ImmutableMap.of(
                "teacher_1", ImmutableMap.of(
                        "Math", 4,
                        "Physics", 3),
                "teacher_2", ImmutableMap.of(
                        "Biology", 3,
                        "PE", 1));
        Map<String, UserDTO> teachers = ImmutableMap.of(
                "teacher_1", getUserDTO("teacher_1", "Math", "Physics"),
                "teacher_2", getUserDTO("teacher_2", "Biology", "PE"));
        Set<String> realSubjects = Sets.newHashSet("Math", "Physics", "Biology"); // <-- PE is missing

        // THEN
        Assertions.assertThrows(BadRequestException.class,
                () -> service.validateSubjects(realSubjects, teachers, info));
    }

    @Test
    void shouldThrowExceptionOnMissingTeachers() {
        // GIVEN
        Map<String, Map<String, Integer>> info = ImmutableMap.of(
                "teacher_1", ImmutableMap.of(
                        "Math", 4,
                        "Physics", 3),
                "teacher_2", ImmutableMap.of(
                        "Biology", 3,
                        "PE", 1));
        Map<String, UserDTO> teachers = ImmutableMap.of(
                // teacher_1 is missing
                "teacher_2", getUserDTO("teacher_2", "Biology", "PE"));
        Set<String> realSubjects = Sets.newHashSet("Math", "Physics", "Biology", "PE");

        // THEN
        Assertions.assertThrows(IllegalStateException.class,
                () -> service.validateSubjects(realSubjects, teachers, info));
    }

    @Test
    void shouldThrowExceptionWhenTeacherIsAssignedToASubjectTheyDontTeach() {
        // GIVEN
        Map<String, Map<String, Integer>> info = ImmutableMap.of(
                "teacher_1", ImmutableMap.of(
                        "Math", 4,
                        "Physics", 3),  // <-- teacher_1 is assigned Physics
                "teacher_2", ImmutableMap.of(
                        "Biology", 3,
                        "PE", 1));
        Map<String, UserDTO> teachers = ImmutableMap.of(
                "teacher_1", getUserDTO("teacher_1", "Math"),   // <-- teacher_1 doesn't teach Physics
                "teacher_2", getUserDTO("teacher_2", "Biology", "PE"));
        Set<String> realSubjects = Sets.newHashSet("Math", "Physics", "Biology", "PE");

        // THEN
        Assertions.assertThrows(BadRequestException.class,
                () -> service.validateSubjects(realSubjects, teachers, info));
    }

    @Test
    void shouldConvertRequestInfoToFlatListOfSubjects() {
        // GIVEN
        Map<String, Map<String, Integer>> info = ImmutableMap.of(
                "teacher_1", ImmutableMap.of(
                        "Math", 2,
                        "Physics", 1),  // <-- teacher_1 is assigned Physics
                "teacher_2", ImmutableMap.of(
                        "Biology", 1,
                        "PE", 2));

        Multiset<TeacherWithSubject> expectedSubjects = HashMultiset.create();
        expectedSubjects.add(new TeacherWithSubject("teacher_1", "Math"));
        expectedSubjects.add(new TeacherWithSubject("teacher_1", "Math"));
        expectedSubjects.add(new TeacherWithSubject("teacher_1", "Physics"));
        expectedSubjects.add(new TeacherWithSubject("teacher_2", "Biology"));
        expectedSubjects.add(new TeacherWithSubject("teacher_2", "PE"));
        expectedSubjects.add(new TeacherWithSubject("teacher_2", "PE"));

        // WHEN
        Multiset<TeacherWithSubject> realSubjects = service.convertToFlatList(info);

        // THEN
        assertThat(expectedSubjects).isEqualTo(realSubjects);
    }

    private UserDTO getUserDTO(String id, String... subjects) {
        return UserDTO.builder()
                .id(id)
                .customAttributes(CustomAttributesDTO.builder()
                        .subjects(Arrays.asList(subjects))
                        .build())
                .firstName("firstName")
                .role(UserDTO.Role.TEACHER)
                .pesel("12312312312")
                .lastName("lastName")
                .userName("userName")
                .build();
    }
}
