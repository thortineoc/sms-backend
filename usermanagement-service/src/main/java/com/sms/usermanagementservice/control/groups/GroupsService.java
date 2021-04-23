package com.sms.usermanagementservice.control.groups;

import com.google.common.collect.Lists;
import com.sms.clients.KeycloakClient;
import com.sms.clients.entity.UserSearchParams;
import com.sms.usermanagementservice.entity.Group;
import com.sms.usermanagementservice.entity.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;


import java.util.List;
import java.util.stream.Collectors;

@Component
@Scope("request")
public class GroupsService {

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private KeycloakClient keycloakClient;

    public List<String> getAll() {

        return Lists.newArrayList(groupRepository.findAll())
                .stream()
                .map(Group::getName)
                .collect(Collectors.toList());
    }

    public void create(String group) {
        try {
            Group newGroup = new Group(group);
            groupRepository.save(newGroup);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }

    }

    public void delete(String name) {

        try {
            Group groupToDelete = new Group(name);
            groupRepository.delete(groupToDelete);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
    }
}
