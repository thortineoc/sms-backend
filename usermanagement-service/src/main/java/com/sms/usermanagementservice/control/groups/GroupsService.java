package com.sms.usermanagementservice.control.groups;

import com.google.common.collect.Lists;
import com.sms.usermanagement.GroupDTO;
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

    public List<GroupDTO> getAll() {

        return Lists.newArrayList(groupRepository.findAll())
                .stream()
                .map(Group::getGroupDTO)
                .collect(Collectors.toList());

    }

    public void create(GroupDTO group) {
        try {
            Group newGroup = new Group(group.getName());
            groupRepository.save(newGroup);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }

    }

    public void delete(Integer id) {
        //TODO: check if group is used
        try {
            Group groupToDelete = new Group(id);
            groupRepository.delete(groupToDelete);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
    }
}
