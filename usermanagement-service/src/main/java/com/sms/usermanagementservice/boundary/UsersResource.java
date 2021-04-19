package com.sms.usermanagementservice.boundary;

import com.sms.context.UserContext;
import com.sms.usermanagement.UserDTO;
import com.sms.usermanagementservice.control.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.security.auth.login.LoginException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import java.util.Map;
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
    public void FilterGet(@Context UriInfo ui) {
       validateRole();
        MultivaluedMap<String, String> queryParams=ui.getQueryParameters();
        //ewentualnie https://stackoverflow.com/questions/55103757/urlsearchparams-returning-null-for-the-first-query-string
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