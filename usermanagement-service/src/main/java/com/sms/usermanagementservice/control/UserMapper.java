package com.sms.usermanagementservice.control;

import com.google.common.collect.ImmutableMap;
import com.sms.authlib.UserDTO;
import com.sms.usermanagementservice.entity.User;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.ArrayList;
import java.util.Collections;

public class UserMapper {

    public static UserDTO toDTO(User user) {
        return UserDTO.builder()
                .userName(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .roles(user.getRoles())
                .email(user.getEmail())
                .group(user.getGroup())
                .build();
    }

    public static User toUser(UserDTO user) {
        return User.builder()
                .username(user.getUserName())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .roles(user.getRoles())
                .email(user.getEmail())
                .group(user.getGroup())
                .build();
    }

    public static UserRepresentation toUserRepresentation(User user, String password) {
        UserRepresentation userRep = new UserRepresentation();
        userRep.setEmail(user.getEmail());
        userRep.setUsername(user.getUsername());
        userRep.setFirstName(user.getFirstName());
        userRep.setLastName(user.getLastName());
        userRep.setRealmRoles(new ArrayList<>(user.getRoles()));
        userRep.setCredentials(Collections.singletonList(getPasswordCredential(password)));

        // TODO: make sure this works and users returned from keycloak have this attribute mapped
        userRep.setAttributes(ImmutableMap.of("group", Collections.singletonList(user.getGroup())));
        return userRep;
    }

    private static CredentialRepresentation getPasswordCredential(String password) {
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType("password");
        credential.setValue(password);
        credential.setTemporary(false);
        return credential;
    }
}
