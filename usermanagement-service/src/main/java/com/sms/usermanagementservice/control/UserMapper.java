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

    public static User toUserFromUserRepresentation(UserRepresentation user) {
        return   User.builder()
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .userAttributes(mapUserAttributesR(user))
                .email(Optional.ofNullable(user.getEmail()))
                .role(Role(user.getAttributes().get("role").get(0)))
                .build();
    }


    private static UserDTO.Role Role(String tmp) {
        if (tmp.equalsIgnoreCase("student")) return UserDTO.Role.STUDENT;
        if (tmp.equalsIgnoreCase("teacher")) return UserDTO.Role.TEACHER;
        if (tmp.equalsIgnoreCase("parent")) return UserDTO.Role.PARENT;
        else
            return UserDTO.Role.ADMIN;
    }

    public static Map<String, String> mapUserAttributesR(UserRepresentation user) {

            Map<String, String> userAttributes = new HashMap<>();
            Map<String, List<String>> customAttributes = user.getAttributes();
            if(customAttributes.containsKey("pesel"))
                userAttributes.put("pesel", customAttributes.get("pesel").get(0));
            if(customAttributes.containsKey("role"))
                userAttributes.put("role", Role(customAttributes.get("role").get(0)).toString());
            if(customAttributes.containsKey("middleName"))
                userAttributes.put("middleName", customAttributes.get("middleName").get(0));
            if(customAttributes.containsKey("phoneNumber"))
                userAttributes.put("phoneNumber", customAttributes.get("phoneNumber").get(0));

            switch (Role(user.getAttributes().get("role").get(0))) {
                case STUDENT:
                    userAttributes.put("group", customAttributes.get("group").get(0));
                    userAttributes.put("relatedUser", customAttributes.get("relatedUser").get(0));
                    break;
                case TEACHER:
                    if (customAttributes.get("subjects") != null && !customAttributes.get("subjects").isEmpty()) {
                        userAttributes.put("subjects", String.join(",", customAttributes.get("subjects")));
                    }
                    break;
                case ADMIN:
                case PARENT:
                    break;
            }

            return userAttributes;
        }


    public static UserRepresentation toUserRepresentation(UserDTO user, String username, String password) {
        UserRepresentation userRep = new UserRepresentation();
        userRep.setUsername(username);
        userRep.setFirstName(user.getFirstName());
        userRep.setLastName(user.getLastName());
        user.getEmail().ifPresent(userRep::setEmail);
        userRep.setCredentials(Collections.singletonList(getPasswordCredential(password)));

        userRep.setAttributes(asMultimap(mapUserAttributes(user)));

        return userRep;
    }



    public static UserRepresentation toParentRepresentationFromStudent(UserDTO user, String username, String password) {
        UserRepresentation userRep = new UserRepresentation();
        userRep.setUsername(username);
        userRep.setFirstName("Parent");
        userRep.setLastName(user.getLastName());
        userRep.setCredentials(Collections.singletonList(getPasswordCredential(password)));

        userRep.setAttributes(asMultimap(mapParentAttributesFromStudent(user)));

        return userRep;
    }

    private static Map<String, String> mapUserAttributes(UserDTO user) {
        Map<String, String> userAttributes = new HashMap<>();
        CustomAttributesDTO customAttributes = user.getCustomAttributes();
        userAttributes.put("pesel", user.getPesel());
        userAttributes.put("role", user.getRole().toString());
        customAttributes.getMiddleName().ifPresent(p -> userAttributes.put("middleName", p));
        customAttributes.getPhoneNumber().ifPresent(p -> userAttributes.put("phoneNumber", p));

        switch (user.getRole()) {
            case STUDENT:
                customAttributes.getGroup().ifPresent(p -> userAttributes.put("group", p));
                customAttributes.getRelatedUser().ifPresent(p -> userAttributes.put("relatedUser", p));
                break;
            case TEACHER:
                if (customAttributes.getSubjects() != null && !customAttributes.getSubjects().isEmpty()) {
                    userAttributes.put("subjects", String.join(",", customAttributes.getSubjects()));
                }
                break;
            case ADMIN:
            case PARENT:
                break;
        }

        return userAttributes;
    }

    private static Map<String, String> mapParentAttributesFromStudent(UserDTO user) {
        Map<String, String> userAttributes = new HashMap<>();
        userAttributes.put("pesel", "parent_" + user.getPesel());
        userAttributes.put("role", UserDTO.Role.PARENT.toString());

        return userAttributes;
    }

    private static CredentialRepresentation getPasswordCredential(String password) {
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType("password");
        credential.setValue(password);
        credential.setTemporary(false);
        return credential;
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

    private static Map<String, List<String>> asMultimap(Map<String, String> map) {
        return map.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> Collections.singletonList(e.getValue())));
    }
}
