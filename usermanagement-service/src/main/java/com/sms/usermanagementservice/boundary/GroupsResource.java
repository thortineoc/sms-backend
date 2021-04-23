package com.sms.usermanagementservice.boundary;

import com.sms.context.UserContext;

import com.sms.usermanagementservice.control.groups.GroupsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
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
    public ResponseEntity<Object> getGroups() {

        List<String> list = groupsService.getAll();
        if(list.isEmpty()){
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok().body(list);
    }

    @PostMapping(value = "/{name}")
    public ResponseEntity<String> newGroup(@PathVariable String name) {
        validateRole();
        groupsService.create(name);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<String> deleteGroup(@PathVariable String id) {
        validateRole();
        groupsService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private void validateRole() {
        if (!userContext.getSmsRole().equals("ADMIN")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }
}
