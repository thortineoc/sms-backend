package com.sms.context;

import com.sms.authlib.UserAuthDTO;
import com.sms.usermanagement.UserDTO;
import org.keycloak.KeycloakPrincipal;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Set;

@Component
public class UserContext {

    private String userId;
    private String userName;
    private String token;
    private Set<String> kcRoles;
    private UserDTO.Role smsRole;
    private Map<String, Object> customAttributes;

    void fromHttpRequest(HttpServletRequest request) {
        if ("KEYCLOAK".equals(request.getAuthType())) {
            KeycloakPrincipal<?> keycloakPrincipal = (KeycloakPrincipal<?>) request.getUserPrincipal();

            this.customAttributes = keycloakPrincipal.getKeycloakSecurityContext().getToken().getOtherClaims();
            if (customAttributes.containsKey("role")) {
                this.smsRole = UserDTO.Role.valueOf(customAttributes.get("role").toString());
            }
            this.userId = keycloakPrincipal.getName();
            this.token = keycloakPrincipal.getKeycloakSecurityContext().getTokenString();
            this.kcRoles = keycloakPrincipal.getKeycloakSecurityContext().getToken().getRealmAccess().getRoles();
            this.userName = keycloakPrincipal.getKeycloakSecurityContext().getToken().getPreferredUsername();
        }
    }

    void clear() {
        this.userId = null;
        this.userName = null;
        this.token = null;
        this.kcRoles = null;
        this.smsRole = null;
        this.customAttributes = null;
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
