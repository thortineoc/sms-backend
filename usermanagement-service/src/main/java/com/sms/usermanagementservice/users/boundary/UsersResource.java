package com.sms.usermanagementservice.users.boundary;

import com.sms.api.common.BadRequestException;
import com.sms.context.AuthRole;
import com.sms.api.usermanagement.UserDTO;
import com.sms.api.usermanagement.UsersFiltersDTO;
import com.sms.usermanagementservice.users.control.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Set;


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
                throw new BadRequestException("Cannot create user with role: " + data.getRole());
        }

        return ResponseEntity.noContent().build();
    }

    @AuthRole(UserDTO.Role.ADMIN)
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable("id") String id) {
        usersService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/filter", produces = MediaType.APPLICATION_JSON, consumes = MediaType.APPLICATION_JSON)
    public ResponseEntity<List<UserDTO>> filterUsers(@RequestBody UsersFiltersDTO filterParamsDTO) {
        List<UserDTO> users = usersService.filterUserByParameters(filterParamsDTO);
        if (users.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(users);
        }
    }

    // TODO: test this endpoint
    @GetMapping("/ids")
    public ResponseEntity<List<UserDTO>> getUsers(@RequestParam("id") Set<String> ids) {
        List<UserDTO> users = usersService.getUsers(ids);
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

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable("id") String id) {
        return usersService.getUser(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }
}