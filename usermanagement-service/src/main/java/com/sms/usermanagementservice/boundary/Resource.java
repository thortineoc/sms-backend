package com.sms.usermanagementservice.boundary;

import com.sms.usermanagementservice.control.UsersService;
import com.sms.usermanagementservice.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class Resource {

    @Autowired
    UsersService userService ;


    @GetMapping("/user/{id}")
    @ResponseBody
    public void user(@PathVariable("id") String id){
        UsersService.getUserById(id);
    }

    @GetMapping("/group/{groupID}")
    @ResponseBody
    public void group(@PathVariable("groupID") String groupID){
        UsersService.getGroup(groupID);
    }

    @GetMapping("/teachers")
    public void teachers(){
        UsersService.getTeachers();
    }

    @GetMapping("role/{rola}")
    @ResponseBody
    public void role(@PathVariable("rola") String rola){
        UsersService.getUsersByRole(rola);
    }

    @GetMapping("/teachers/{id}")
    @ResponseBody
    public void teacher(@PathVariable("id") String id){
        UsersService.getTeacher(id);
    }

}
