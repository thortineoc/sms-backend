package com.sms.usermanagementservice.control;

import com.sms.usermanagement.GroupDTO;
import com.sms.usermanagementservice.entity.Group;
import com.sms.usermanagementservice.entity.GroupDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
@Scope("request")
public class GroupsService {

    @Autowired
    private GroupDao groupDao;

    public void create(GroupDTO group){
        try {
            Group newGroup = new Group(group.getName());
            groupDao.save(newGroup);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }

    }

    public void delete(GroupDTO group){
        try {
            Group groupToDelete = new Group(group.getId());
            groupDao.delete(groupToDelete);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
    }
}
