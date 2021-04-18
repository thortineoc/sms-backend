package com.sms.usermanagementservice.boundary;

import com.sms.context.UserContext;
import com.sms.usermanagementservice.control.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class Resource {

    @Autowired
    private UsersService userService;
    @Autowired
    private UserContext userContext;

    @GetMapping("/{id}")
    @ResponseBody
    public void user(@PathVariable("id") String id){
        UsersService.getUserById(id);
    }

    @GetMapping("/{string}")
    @ResponseBody
    public void roleOrGroup(@PathVariable("string") String string){
        UsersService.getString(string);
    }

    @GetMapping("/search/{object}") //tylko do maila/username/first name/last name defaultowe keycloaka
    @ResponseBody
    public void searchUser(@PathVariable("object") String object){
        UsersService.getUser(object);
    }

    @GetMapping("/{String1}/{String2}")
    @ResponseBody
    public void roleAndGroup(@PathVariable("String1") String object, @PathVariable("String2") String object2){
        UsersService.getRoleGroup(object, object2);
    }


}
