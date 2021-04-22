package com.sms.usermanagementservice.control.groups;

import com.sms.usermanagement.GroupDTO;
import com.sms.usermanagementservice.entity.Group;
import com.sms.usermanagementservice.entity.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Component
@Scope("request")
public class GroupsService {

    @Autowired
    private GroupRepository groupRepository;

    public List<GroupDTO> getAll(){

        List<GroupDTO> listOfGroupDTO = new ArrayList<>();
        List<Group> groups = groupRepository.findAll();
        for(Group group: groups){
            listOfGroupDTO.add(group.getGroupDTO());
        }
        if(listOfGroupDTO.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NO_CONTENT);
        }
        return listOfGroupDTO;
    }

    public void create(GroupDTO group){
        try {
            Group newGroup = new Group(group.getName());
            groupRepository.save(newGroup);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }

    }

    public void delete(GroupDTO group){
        //TODO: check if group is used
        try {
            Group groupToDelete = new Group(group.getId());
            groupRepository.delete(groupToDelete);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
    }
}
