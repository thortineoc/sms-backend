package com.sms.usermanagementservice.boundary;

import com.sms.usermanagementservice.control.UsersService;
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


    @GetMapping("role/{rola}")
    @ResponseBody
    public void role(@PathVariable("rola") String rola){
        UsersService.getUsersByRole(rola);
    }

    @GetMapping("/search/{object}")
    @ResponseBody
    public void searchUser(@PathVariable("object") String object){
        UsersService.searchUser(object);
    }

}
