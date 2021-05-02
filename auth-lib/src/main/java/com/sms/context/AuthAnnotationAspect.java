package com.sms.context;

import com.sms.usermanagement.UserDTO;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;

@Aspect
@Component
public class AuthAnnotationAspect {

    @Around("@annotation(AuthRole)")
    public Object checkRole(ProceedingJoinPoint joinPoint) throws Throwable {
        UserContext context = UserContext.get();
        List<UserDTO.Role> roles = Arrays.asList(((MethodSignature) joinPoint.getSignature()).getMethod()
                .getAnnotation(AuthRole.class).value());

        if (!roles.contains(context.getSmsRole())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "User did not have any of the required roles: " + roles);
        }
        return joinPoint.proceed();
    }
}
