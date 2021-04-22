package com.sms.usermanagementservice.boundary;

import com.sms.context.UserContext;
import com.sms.usermanagement.UserDTO;
import com.sms.usermanagementservice.control.UsersService;
import com.sms.usermanagementservice.entity.User;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.PostConstruct;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/users")
@Scope("request")
public class UsersResource {


    @Autowired
    private UsersService usersService;

    @Autowired
    private UserContext userContext;

/*   @PostMapping
    public List<UserDTO> filterUsers(@RequestParam Map<String, String> queryParam) {

       validateRole();
       return usersService.filterUserByParameters(queryParam);
    }*/

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

    private void validateRole() {
        if (!userContext.getSmsRole().equals("ADMIN")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

}