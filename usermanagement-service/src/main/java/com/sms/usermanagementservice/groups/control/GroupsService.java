package com.sms.usermanagementservice.groups.control;

import com.sms.clients.KeycloakClient;
import com.sms.clients.entity.UserSearchParams;
import com.sms.api.usermanagement.UserDTO;
import com.sms.usermanagementservice.clients.TimetablesClient;
import com.sms.usermanagementservice.groups.control.repository.GroupJPA;
import com.sms.usermanagementservice.groups.control.repository.GroupRepository;
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
public class GroupsService {

    private static final String GROUP = "group";

    @Autowired
    GroupRepository groupRepository;

    @Autowired
    KeycloakClient keycloakClient;

    @Autowired
    TimetablesClient timetablesClient;

    public List<String> getAll() {
        return groupRepository.findAllByOrderByNameAsc()
                .stream()
                .map(GroupJPA::getName)
                .collect(Collectors.toList());
    }

    public void create(String group) {
        if (groupRepository.existsById(group)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Group: " + group + " already exists.");
        }
        groupRepository.save(new GroupJPA(group));
    }

    public List<String> delete(String name) {
        groupRepository.deleteById(name);
        Map<Boolean, List<UserRepresentation>> updateResults = getStudentsWithGroups(name).stream()
                .map(this::removeGroup)
                .collect(Collectors.groupingBy(student -> keycloakClient.updateUser(student.getId(), student)));
        boolean timetableIsDeleted = timetablesClient.deleteTimetable(name);
        if (!timetableIsDeleted) {
            throw new IllegalStateException("Timetable for group: " + name + " couldn't be deleted.");
        }
        return UserUtils.getFailedUserIds(updateResults);
    }

    private UserRepresentation removeGroup(UserRepresentation user) {
        Map<String, List<String>> attributes = new HashMap<>(user.getAttributes());
        attributes.remove(GROUP);
        user.setAttributes(attributes);
        return user;
    }

    private List<UserRepresentation> getStudentsWithGroups(String group) {
        return keycloakClient.getUsers(new UserSearchParams()).stream()
                .filter(user -> UserUtils.isRoleAndHasAttribute(user, UserDTO.Role.STUDENT, GROUP, group))
                .collect(Collectors.toList());
    }
}
