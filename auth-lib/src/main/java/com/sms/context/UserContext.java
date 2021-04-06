package com.sms.context;

import org.keycloak.KeycloakPrincipal;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Set;

@Component
public class UserContext {

    private String userId;
    private String userName;
    private String token;
    private Set<String> roles;

    void fromHttpRequest(HttpServletRequest request) {
        if ("KEYCLOAK".equals(request.getAuthType())) {
            KeycloakPrincipal<?> keycloakPrincipal = (KeycloakPrincipal<?>) request.getUserPrincipal();

            this.userId = keycloakPrincipal.getName();
            this.token = keycloakPrincipal.getKeycloakSecurityContext().getTokenString();
            this.roles = keycloakPrincipal.getKeycloakSecurityContext().getToken().getRealmAccess().getRoles();
            this.userName = keycloakPrincipal.getKeycloakSecurityContext().getToken().getPreferredUsername();
        }
    }

    void clear() {
        this.userId = null;
        this.userName = null;
        this.token = null;
        this.roles = null;
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

    public Set<String> getRoles() {
        return roles;
    }
}
