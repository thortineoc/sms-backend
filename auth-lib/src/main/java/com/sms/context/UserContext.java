package com.sms.context;

import com.sms.authlib.UserAuthDTO;
import com.sms.usermanagement.UserDTO;
import org.keycloak.KeycloakPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Set;

@Component
@Scope("request")
public class UserContext {

    private static final String ROLE = "role";

    public static ThreadLocal<UserContext> context = new ThreadLocal<>();

    private String userId;
    private String userName;
    private String token;
    private Set<String> kcRoles;
    private UserDTO.Role smsRole;
    private Map<String, Object> customAttributes;

    @Autowired
    SecurityConfig securityConfig;

    @PostConstruct
    private void fromPrincipal() {
        KeycloakPrincipal<?> keycloakPrincipal = securityConfig.getPrincipal();

        this.customAttributes = keycloakPrincipal.getKeycloakSecurityContext().getToken().getOtherClaims();
        if (customAttributes.containsKey(ROLE)) {
            this.smsRole = UserDTO.Role.valueOf(customAttributes.get(ROLE).toString());
        }
        this.userId = keycloakPrincipal.getName();
        this.token = keycloakPrincipal.getKeycloakSecurityContext().getTokenString();
        this.kcRoles = keycloakPrincipal.getKeycloakSecurityContext().getToken().getRealmAccess().getRoles();
        this.userName = keycloakPrincipal.getKeycloakSecurityContext().getToken().getPreferredUsername();

        UserContext.context.set(this);
    }

    public static UserContext get() {
        return context.get();
    }

    public UserDTO.Role getSmsRole() {
        return smsRole;
    }

    public String getToken() {
        return token;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public Set<String> getKcRoles() {
        return kcRoles;
    }

    public Map<String, Object> getCustomAttributes() {
        return customAttributes;
    }

    public UserAuthDTO toUserAuthDTO() {
        return UserAuthDTO.builder()
                .smsRole(smsRole)
                .roles(kcRoles)
                .userId(userId)
                .userName(userName)
                .token(token)
                .build();
    }
}
