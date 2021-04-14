package com.sms.usermanagementservice.control;

import com.sms.usermanagement.*;
import com.sms.usermanagementservice.entity.User;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.*;
import java.util.stream.Collectors;

public class UserMapper {

    private UserMapper() {
    }

    public static UserDTO toDTO(User user) {
        return UserDTO.builder()
                .userName(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole())
                .email(user.getEmail())
                .customAttributes(mapUserAttributes(user.getUserAttributes()))
                .build();
    }

    public static User toUser(UserDTO user) {
        return User.builder()
                .username(user.getUserName())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .userAttributes(mapUserAttributes(user))
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }

    private static CustomAttributesDTO mapUserAttributes(Map<String, String> attributes) {
        return CustomAttributesDTO.builder()
                .group(Optional.ofNullable(attributes.get("group")))
                .middleName(Optional.ofNullable(attributes.get("middleName")))
                .phoneNumber(Optional.ofNullable(attributes.get("phoneNumber")))
                .subjects(Arrays.asList(attributes.get("subjects").split(",")))
                .relatedUser(Optional.ofNullable(attributes.get("relatedUser")))
                .build();
    }

    private static Map<String, String> mapUserAttributes(UserDTO user) {
        Map<String, String> userAttributes = new HashMap<>();
        CustomAttributesDTO customAttributes = user.getCustomAttributes();
        customAttributes.getMiddleName().ifPresent(p -> userAttributes.put("middleName", p));
        customAttributes.getPhoneNumber().ifPresent(p -> userAttributes.put("phoneNumber", p));
        customAttributes.getGroup().ifPresent(p -> userAttributes.put("group", p));
        customAttributes.getRelatedUser().ifPresent(p -> userAttributes.put("relatedUser", p));
        if (customAttributes.getSubjects() != null && !customAttributes.getSubjects().isEmpty()) {
            userAttributes.put("subjects", String.join(",", customAttributes.getSubjects()));
        }

        return userAttributes;
    }

    public static UserRepresentation toUserRepresentation(User user, String password) {
        UserRepresentation userRep = new UserRepresentation();
        userRep.setUsername(user.getUsername());
        userRep.setFirstName(user.getFirstName());
        userRep.setLastName(user.getLastName());
        user.getEmail().ifPresent(userRep::setEmail);
        userRep.setCredentials(Collections.singletonList(getPasswordCredential(password)));

        // NOTE: due to a bug in keycloak we have to put the role in custom attributes to avoid unnecessary API calls
        Map<String, String> attributes = new HashMap<>(user.getUserAttributes());
        attributes.put("role", user.getRole().toString());

        Map<String, List<String>> customAttributes = user.getUserAttributes().entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> Collections.singletonList(e.getValue())));

        userRep.setAttributes(customAttributes);
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
