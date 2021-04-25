package com.sms.usermanagementservice.control.subjects;

import com.sms.clients.KeycloakClient;
import com.sms.clients.entity.UserSearchParams;
import com.sms.usermanagement.UserDTO;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Component
@Scope("request")
public class SubjectsService {

    private static final String ROLE = "role";
    private static final String SUBJECTS = "subjects";

    @Autowired
    SubjectsRepository subjectsRepository;

    @Autowired
    KeycloakClient keycloakClient;

    public List<String> getAll() {
        return subjectsRepository.findAll().stream()
                .map(SubjectJPA::getName)
                .collect(Collectors.toList());
    }

    public void save(String subject) {
        if (subjectsRepository.existsById(subject)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Subject: " + subject + " already exists");
        }
        subjectsRepository.save(new SubjectJPA(subject));
    }

    public void delete(String subject) {
        subjectsRepository.deleteById(subject);
    }

    public void deleteAll() {
        subjectsRepository.deleteAll();
        if (subjectsRepository.count() != 0) {
            throw new IllegalStateException("Not all subjects have been deleted");
        }
    }

    public List<String> getTeachersWithSubject(String subject) {
        return keycloakClient.getUsers(new UserSearchParams()).stream()
                .filter(user -> isTeacherAndHasSubject(user, subject))
                .map(UserRepresentation::getId)
                .collect(Collectors.toList());
    }

    boolean isTeacherAndHasSubject(UserRepresentation user, String subject) {
        Map<String, List<String>> attributes = user.getAttributes();
        UserDTO.Role role = Optional.ofNullable(attributes.get(ROLE)).map(list -> list.stream()
                    .findFirst()
                    .orElseThrow(noRoleException(user)))
                .map(UserDTO.Role::valueOf)
                .orElseThrow(noRoleException(user));

        boolean hasSubject = Optional.ofNullable(attributes.get(SUBJECTS))
                .map(subjects -> subjects.contains(subject))
                .orElse(false);

        return UserDTO.Role.TEACHER.equals(role) && hasSubject;
    }

    private Supplier<RuntimeException> noRoleException(UserRepresentation user) {
        return () -> new IllegalStateException("User: " + user.getId() + " does not have a role");
    }
}