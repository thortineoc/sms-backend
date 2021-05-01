package com.sms.usermanagementservice.users.boundary;

import com.sms.context.AuthRole;
import com.sms.usermanagement.UserDTO;
import com.sms.usermanagement.UsersFiltersDTO;
import com.sms.usermanagementservice.users.control.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;


@RestController
@RequestMapping("/users")
@Scope("request")
public class UsersResource {

    @Autowired
    private UsersService usersService;

    @PostMapping
    @AuthRole(UserDTO.Role.ADMIN)
    public ResponseEntity<String> newUser(@RequestBody UserDTO data) {
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

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @AuthRole(UserDTO.Role.ADMIN)
    public ResponseEntity<Object> deleteUser(@PathVariable("id") String id) {
        usersService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/filter")
    public ResponseEntity<List<UserDTO>> filterUsers(@RequestBody UsersFiltersDTO filterParamsDTO) {
        List<UserDTO> users = usersService.filterUserByParameters(filterParamsDTO);
        if (users.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(users);
        }
    }

    @PutMapping("/update")
    @AuthRole(UserDTO.Role.ADMIN)
    public ResponseEntity<String> updateUser(@RequestBody UserDTO data) {
        usersService.updateUser(data);
        return ResponseEntity.noContent().build();
    }

}