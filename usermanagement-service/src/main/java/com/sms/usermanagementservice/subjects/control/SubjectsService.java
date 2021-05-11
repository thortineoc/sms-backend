package com.sms.usermanagementservice.subjects.control;

import com.sms.clients.KeycloakClient;
import com.sms.clients.entity.UserSearchParams;
import com.sms.usermanagement.UserDTO;
import com.sms.usermanagementservice.clients.GradesClient;
import com.sms.usermanagementservice.subjects.control.repository.SubjectJPA;
import com.sms.usermanagementservice.subjects.control.repository.SubjectsRepository;
import com.sms.usermanagementservice.users.control.UserUtils;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Scope("request")
public class SubjectsService {

    private static final String SUBJECTS = "subjects";

    @Autowired
    SubjectsRepository subjectsRepository;

    @Autowired
    KeycloakClient keycloakClient;

    @Autowired
    GradesClient gradesClient;

    public List<String> getAll() {
        return subjectsRepository.findAllByOrderByNameAsc().stream()
                .map(SubjectJPA::getName)
                .collect(Collectors.toList());
    }

    public void save(String subject) {
        if (subjectsRepository.existsById(subject)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Subject: " + subject + " already exists");
        }
        subjectsRepository.save(new SubjectJPA(subject));
    }

    public List<String> delete(String subject) {
        subjectsRepository.deleteById(subject);

        Map<Boolean, List<UserRepresentation>> updateResults = getTeachersWithSubject(subject).stream()
                .map(teacher -> removeSubject(teacher, subject))
                .collect(Collectors.groupingBy(teacher -> keycloakClient.updateUser(teacher.getId(), teacher)));

        if (!gradesClient.deleteGradesBySubject(subject)) {
            throw new IllegalStateException("Deleting all grades with subject: " + subject + " failed");
        }
        return UserUtils.getFailedUserIds(updateResults);
    }

    public void deleteAll() {
        subjectsRepository.deleteAll();
        if (subjectsRepository.count() != 0) {
            throw new IllegalStateException("Not all subjects have been deleted");
        }
    }

    private UserRepresentation removeSubject(UserRepresentation user, String subject) {
        List<String> newSubjects = new ArrayList<>(user.getAttributes().get(SUBJECTS));
        newSubjects.remove(subject);
        Map<String, List<String>> attributes = new HashMap<>(user.getAttributes());
        attributes.put(SUBJECTS, newSubjects);
        user.setAttributes(attributes);
        return user;
    }

    private List<UserRepresentation> getTeachersWithSubject(String subject) {
        return keycloakClient.getUsers(new UserSearchParams()).stream()
                .filter(user -> UserUtils.isRoleAndHasAttribute(user, UserDTO.Role.TEACHER, SUBJECTS, subject))
                .collect(Collectors.toList());
    }
}