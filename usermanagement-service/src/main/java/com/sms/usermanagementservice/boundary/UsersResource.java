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

    private void validateRole(){
        if (!userContext.getSmsRole().equals("ADMIN")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping("/new")
    public ResponseEntity<String> newUser(@RequestBody UserDTO data) {

        validateRole();

        switch (data.getRole()){
            case STUDENT: {
                if (!usersService.createStudentWithParent(data)) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
                }
                break;
            }

            case ADMIN: {
                if (!usersService.createAdmin(data)) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
                }
                break;
            }

            case TEACHER: {
                if (!usersService.createTeacher(data)) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
                }
                break;
            }

            case PARENT: {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            }
        }

        return ResponseEntity.ok().build();
    }


}