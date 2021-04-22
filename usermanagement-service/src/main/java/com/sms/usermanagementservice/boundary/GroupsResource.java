package com.sms.usermanagementservice.boundary;

import com.sms.context.UserContext;
import com.sms.usermanagement.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


@RestController
@RequestMapping("/groups")
@Scope("request")
public class GroupsResource {

    @Autowired
    private UserContext userContext;

    @GetMapping
    public ResponseEntity<String> getGroups(){
        validateRole();
        return ResponseEntity.ok().build();
    }


    @PostMapping
    public ResponseEntity<String> newGroup(){
        validateRole();
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<String> deleteGroup(){
        validateRole();
        return ResponseEntity.ok().build();
    }


    private void validateRole() {
        if (!userContext.getSmsRole().equals("ADMIN")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }
}
