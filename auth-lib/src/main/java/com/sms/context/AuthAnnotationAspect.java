package com.sms.context;

import com.sms.usermanagement.UserDTO;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Aspect
@Component
public class AuthAnnotationAspect {

    @Autowired
    private UserContext context;

    @Around("@annotation(AuthRole)")
    public Object checkRole(ProceedingJoinPoint joinPoint) throws Throwable {
        UserDTO.Role role = ((MethodSignature) joinPoint.getSignature()).getMethod()
                .getAnnotation(AuthRole.class).value();

        if (!role.equals(context.getSmsRole())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "User did not have the required role: " + role);
        }
        return joinPoint.proceed();
    }
}
