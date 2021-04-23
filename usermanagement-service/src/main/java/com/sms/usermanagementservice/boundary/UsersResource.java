package com.sms.usermanagementservice.boundary;

import com.sms.context.UserContext;
import com.sms.usermanagement.UserDTO;
import com.sms.usermanagementservice.control.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


@RestController
@RequestMapping("/users")
@Scope("request")
public class UsersResource {


    @Autowired
    private UsersService usersService;

    @Autowired
    private UserContext userContext;

    @PostMapping
    public ResponseEntity<String> newUser(@RequestBody UserDTO data) {

        validateRole();

        switch (data.getRole()) {
            case STUDENT:
                usersService.createStudentWithParent(data);
                break;
            case ADMIN:
            case TEACHER:
                usersService.createUser(data);
                break;
            default:
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok().build();
    }

    @PostMapping("/update")
    public ResponseEntity<String> newUser(@RequestBody UserDTO data) {

        return ResponseEntity.ok().build();
    }
    

    private void validateRole() {
        if (!userContext.getSmsRole().equals("ADMIN")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }
}