package com.sms.usermanagementservice.control.groups;

import com.sms.clients.KeycloakClient;
import com.sms.clients.entity.UserSearchParams;
import com.sms.usermanagement.UserDTO;
import com.sms.usermanagementservice.entity.Group;
import com.sms.usermanagementservice.entity.GroupRepository;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;


import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Component
@Scope("request")
public class GroupsService {

    private static final String ROLE = "role";
    private static final String GROUP = "group";

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private KeycloakClient keycloakClient;

    public List<String> getAll() {

        return groupRepository.findAll()
                .stream()
                .map(Group::getName)
                .collect(Collectors.toList());
    }

    public void create(String group) {

        Group newGroup = new Group(group);
        groupRepository.save(newGroup);

    }

    public void delete(String name) {

        groupRepository.deleteById(name);

    }


    public List<String> getStudentsWithGroups(String group) {
        return keycloakClient.getUsers(new UserSearchParams()).stream()
                .filter(user -> isStudentAndHasGroup(user, group))
                .map(UserRepresentation::getId)
                .collect(Collectors.toList());
    }

    boolean isStudentAndHasGroup(UserRepresentation user, String group) {
        Map<String, List<String>> attributes = user.getAttributes();
        UserDTO.Role role = Optional.ofNullable(attributes.get(ROLE)).map(list -> list.stream()
                .findFirst()
                .orElseThrow(noRoleException(user)))
                .map(UserDTO.Role::valueOf)
                .orElseThrow(noRoleException(user));

        boolean hasGroup = Optional.ofNullable(attributes.get(GROUP))
                .map(groups -> groups.contains(group))
                .orElse(false);

        return UserDTO.Role.STUDENT.equals(role) && hasGroup;
    }

    private Supplier<RuntimeException> noRoleException(UserRepresentation user) {
        return () -> new IllegalStateException("User: " + user.getId() + " does not have a role");
    }
}
