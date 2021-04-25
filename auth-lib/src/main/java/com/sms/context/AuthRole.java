package com.sms.context;

import com.sms.usermanagement.UserDTO;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthRole {
    UserDTO.Role value();
}
