package com.sms.usermanagementservice.boundary;

import com.sms.clients.KeycloakClient;
import com.sms.context.UserContext;
import com.sms.usermanagement.UserDTO;
import com.sms.usermanagementservice.control.UserMapper;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/users")
@Scope("request")
public class UsersResource {

    @Autowired
    private KeycloakClient keycloakClient;

    @Autowired
    private UsersService usersService;

    @Autowired
    private UserContext userContext;

    @PostMapping("/new-student")
    public ResponseEntity<String> newStudent(@RequestBody UserDTO data) {

        if(!userContext.getSmsRole().equals("ADMIN")){
            System.out.print("USER IS NOT AN ADMIN");
            return ResponseEntity.status(403).build();
        }

        if(data.getRole() != UserDTO.Role.STUDENT){
            System.out.print("REQUEST DOES NOT CONTAINING STUDENT");
            return ResponseEntity.badRequest().build();
        }

        if(!data.getCustomAttributes().getSubjects().isEmpty()){
            System.out.print("DATA CONTAINING SUBJECTS");
            return ResponseEntity.badRequest().build();
        }

        UserRepresentation userRepresentation = UserMapper.toUserRepresentation(data, usersService.calculatePassword(data.getFirstName(), data.getLastName()));
        //todo create parent
        if(keycloakClient.createUser(userRepresentation)){
            return ResponseEntity.ok().build();
        }

        System.out.print("THERE WAS AN ERROR WHILE CREATING USER");
        return ResponseEntity.badRequest().build();
    }

    @PostMapping("/new-parent")
    public ResponseEntity<String> newParent(@RequestBody UserDTO data) {

        if(!userContext.getSmsRole().equals("ADMIN")){
            System.out.print("USER IS NOT AN ADMIN");
            return ResponseEntity.status(403).build();
        }

        if(data.getRole() != UserDTO.Role.PARENT){
            System.out.print("REQUEST DOES NOT CONTAINING STUDENT");
            return ResponseEntity.badRequest().build();
        }

        if(!data.getCustomAttributes().getSubjects().isEmpty()){
            System.out.print("DATA CONTAINING SUBJECTS");
            return ResponseEntity.badRequest().build();
        }

        if(data.getCustomAttributes().getGroup().isPresent()){
            System.out.print("DATA CONTAINING GROUP");
            return ResponseEntity.badRequest().build();
        }

        UserRepresentation userRepresentation = UserMapper.toUserRepresentation(data, usersService.calculatePassword(data.getFirstName(), data.getLastName()));
        if(keycloakClient.createUser(userRepresentation)){
            return ResponseEntity.ok().build();
        }

        System.out.print("THERE WAS AN ERROR WHILE CREATING USER");
        return ResponseEntity.badRequest().build();
    }

    @PostMapping("/new-teacher")
    public ResponseEntity<String> newTeacher(@RequestBody UserDTO data) {

        if(!userContext.getSmsRole().equals("ADMIN")){
            System.out.print("USER IS NOT AN ADMIN");
            return ResponseEntity.status(403).build();
        }

        if(data.getRole() != UserDTO.Role.TEACHER){
            System.out.print("REQUEST DOES NOT CONTAINING TEACHER");
            return ResponseEntity.badRequest().build();
        }

        if(data.getCustomAttributes().getRelatedUser().isPresent()){
            System.out.print("DATA CONTAINING RELATED USER");
            return ResponseEntity.badRequest().build();
        }

        if(data.getCustomAttributes().getGroup().isPresent()){
            System.out.print("DATA CONTAINING GROUP");
            return ResponseEntity.badRequest().build();
        }

        UserRepresentation userRepresentation = UserMapper.toUserRepresentation(data, usersService.calculatePassword(data.getFirstName(), data.getLastName()));

        if(keycloakClient.createUser(userRepresentation)){
            return ResponseEntity.ok().build();
        }

        System.out.print("THERE WAS AN ERROR WHILE CREATING USER");
        return ResponseEntity.badRequest().build();
    }

    @PostMapping("/new-admin")
    public ResponseEntity<String> newAdmin(@RequestBody UserDTO data) {

        if(!userContext.getSmsRole().equals("ADMIN")){
            System.out.print("USER IS NOT AN ADMIN");
            return ResponseEntity.status(403).build();
        }

        if(data.getRole() != UserDTO.Role.ADMIN){
            System.out.print("REQUEST DOES NOT CONTAINING ADMIN");
            return ResponseEntity.badRequest().build();
        }

        if(data.getCustomAttributes().getRelatedUser().isPresent()){
            System.out.print("DATA CONTAINING RELATED USER");
            return ResponseEntity.badRequest().build();
        }

        if(data.getCustomAttributes().getGroup().isPresent()){
            System.out.print("DATA CONTAINING GROUP");
            return ResponseEntity.badRequest().build();
        }

        if(!data.getCustomAttributes().getSubjects().isEmpty()){
            System.out.print("DATA CONTAINING SUBJECTS");
            return ResponseEntity.badRequest().build();
        }

        UserRepresentation userRepresentation = UserMapper.toUserRepresentation(data, usersService.calculatePassword(data.getFirstName(), data.getLastName()));

        if(keycloakClient.createUser(userRepresentation)){
            return ResponseEntity.ok().build();
        }

        System.out.print("THERE WAS AN ERROR WHILE CREATING USER");
        return ResponseEntity.badRequest().build();
    }


}