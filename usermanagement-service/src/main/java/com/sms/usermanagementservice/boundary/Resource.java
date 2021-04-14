package com.sms.usermanagementservice.boundary;

import com.sms.usermanagementservice.control.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class Resource {

    @Autowired
    UsersService usersService;

    @GetMapping("/")
    public void allUsers(){
        usersService.getAllUsers();
    }

    @GetMapping("/user/{id}")
    @ResponseBody
    public void user(@PathVariable("id") long id){
        usersService.getUser(id);
    }

   /* @GetMapping("/usersid") //endpoint na wszytskie wartości id/ewentualnie potem tego gówna String
    public void allUsersId(){
        usersService.getAllUsersId();
    }
*/ //to raczej nie ma sesnu

    @GetMapping("/group/{groupID}")
    @ResponseBody
    public void group(@PathVariable("groupID") short groupID){
        usersService.getGroup(groupID);
    }

    @GetMapping("/teachers")
    public void teachers(){
        usersService.getTeachers();
    }

    @GetMapping("/teachers/{id}")
    @ResponseBody
    public void teacher(@PathVariable("id") short id){
        usersService.getTeacher(id);
    }

    @GetMapping("role/{rola}")
    @ResponseBody
    public void role)(@PathVariable("rola") String rola="users"){
        usersService.getUsersByRole(rola);
    }

}
