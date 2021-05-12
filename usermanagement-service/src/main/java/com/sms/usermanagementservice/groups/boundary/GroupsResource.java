package com.sms.usermanagementservice.groups.boundary;

import com.sms.context.AuthRole;
import com.sms.context.UserContext;

import com.sms.api.usermanagement.UserDTO;
import com.sms.usermanagementservice.groups.control.GroupsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<List<String>> getGroups() {

        List<String> list = groupsService.getAll();
        if(list.isEmpty()){
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok().body(list);
    }

    @PostMapping( "/{name}")
    @AuthRole(UserDTO.Role.ADMIN)
    public ResponseEntity<String> newGroup(@PathVariable String name) {
        groupsService.create(name);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping( "/{name}")
    @AuthRole(UserDTO.Role.ADMIN)
    public ResponseEntity<List<String>> deleteGroup(@PathVariable String name) {

        List<String> failedStudents = groupsService.delete(name);
        if (failedStudents.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(500).body(failedStudents);
        }
    }
}
