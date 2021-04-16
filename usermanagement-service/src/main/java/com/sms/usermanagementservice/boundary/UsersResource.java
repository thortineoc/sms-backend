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
    // Postmapping nie potrzebny jak jest jeden endpoint już
    @PostMapping("/new")
    public ResponseEntity<String> newUser(@RequestBody UserDTO data) {

        validateRole();

        // troche się to rozwlekło, jak zrobisz że te metody na usersService będą tylko wyjątki rzucały to można np.
        /*
        * switch (data.getRole()) {
        *   case STUDENT: usersService.createStudent...
        *   case ADMIN: ...
        *   case TEACHER: ...
        *   default: throw new IllegalStateException()
        * } */
        // i wychodzi 5 linijek a nie jakieś 20 no nie, + nie masz obsłużonego default:, PARENT też pod to podejdzie
        // ogólnie zwracanie booleana jest spoko jak planujemy coś zrobić z informacją że coś się nie udało,
        // a tutaj jedyne co robimy to rzucamy wyjątek, więc można od razu rzucić wyjątek w usersService no nie
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