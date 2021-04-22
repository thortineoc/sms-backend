package com.sms.usermanagementservice.boundary;

import com.sms.context.UserContext;
import com.sms.usermanagement.GroupDTO;
import com.sms.usermanagementservice.control.GroupsService;
import com.sms.usermanagementservice.entity.Group;
import com.sms.usermanagementservice.entity.GroupDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/groups")
@Scope("request")
public class GroupsResource {

    @Autowired
    private UserContext userContext;

    @Autowired
    private GroupsService groupsService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getGroups(){
        validateRole();

        List <GroupDTO> list = groupsService.getAll();


        return ResponseEntity.ok().body(list);
    }


    @PostMapping
    public ResponseEntity<String> newGroup(@RequestBody GroupDTO group){
        validateRole();
        groupsService.create(group);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<String> deleteGroup(@RequestBody GroupDTO group){
        validateRole();
        groupsService.delete(group);
        return ResponseEntity.ok().build();
    }

    private void validateRole() {
        if (!userContext.getSmsRole().equals("ADMIN")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }
}
