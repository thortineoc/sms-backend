package com.sms.usermanagementservice.boundary;

import com.sms.context.UserContext;
import com.sms.usermanagement.UserDTO;
import com.sms.usermanagementservice.control.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.security.auth.login.LoginException;
import java.util.Optional;


@RestController
@RequestMapping("/users")
@Scope("request")
public class UsersResource {


    @Autowired
    private UsersService usersService;

    @Autowired
    private UserContext userContext;

    // {empty} <-all users
    // {id} <- id <- jak nie znajdzie to imię -> nazwisko
    // {group} <- grupa
    // {role} <- rola
    // {group}/{role} <-grupa, rola
    // + - sortowanie po ostatnim?
    @GetMapping
    public void findByParams(@RequestParam("param1") Optional<String> param1,
                             @RequestParam("param2") Optional<String> param2,
                             @RequestParam("param3") Optional<String> param3,
                             @RequestParam("param4") Optional<String> param4){
       validateRole();
       usersService.match(param1, param2, param3, param4);
    }

    private void validateRole() {
        if (!userContext.getSmsRole().equals("ADMIN")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

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

}