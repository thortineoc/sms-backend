package com.sms.usermanagementservice.boundary;

import com.sms.usermanagementservice.entity.User;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("request")
public class UsersService {

    public String calculatePassword(User user){
        return user.getFirstName().substring(0, Math.min(user.getFirstName().length(), 4)) +
                user.getLastName().substring(0, Math.min(user.getLastName().length(), 4));
    }
}
