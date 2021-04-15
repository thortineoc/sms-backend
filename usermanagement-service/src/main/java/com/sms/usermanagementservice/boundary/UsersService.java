package com.sms.usermanagementservice.boundary;

import com.sms.usermanagement.UserDTO;
import com.sms.usermanagementservice.entity.User;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("request")
public class UsersService {

    public String calculatePassword(String firstName, String lastName){
        return firstName.substring(0, Math.min(firstName.length(), 4)) +
                lastName.substring(0, Math.min(lastName.length(), 4));
    }
}
