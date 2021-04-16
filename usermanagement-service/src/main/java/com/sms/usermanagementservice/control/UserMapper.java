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
    // do usunięcia
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

        Map<String, List<String>> customAttributes = attributes.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> Collections.singletonList(e.getValue())));

        userRep.setAttributes(customAttributes);
        return userRep;
    }

    public static UserRepresentation toUserRepresentation(UserDTO user, String username, String password) {
        UserRepresentation userRep = new UserRepresentation();
        userRep.setUsername(username);
        userRep.setFirstName(user.getFirstName());
        userRep.setLastName(user.getLastName());
        user.getEmail().ifPresent(userRep::setEmail);
        userRep.setCredentials(Collections.singletonList(getPasswordCredential(password)));

        Map<String, String> attributes = mapUserAttributes(user, user.getRole());

        // --- to bym wyrzucił do metody i wywołał ją w mapUserAttributes, podobnie w metodzie niżej bo się powtarza te 5 linijek
        Map<String, List<String>> customAttributes = attributes.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> Collections.singletonList(e.getValue())));

        userRep.setAttributes(customAttributes);
        // wtedy by było userRep.setAttributes(mapUserAttributes(user);

        return userRep;
    }

    public static UserRepresentation toParentRepresentationFromStudent(UserDTO user, String username, String password) {
        UserRepresentation userRep = new UserRepresentation();
        userRep.setUsername(username);
        userRep.setFirstName("Parent");
        userRep.setLastName(user.getLastName());
        userRep.setCredentials(Collections.singletonList(getPasswordCredential(password)));

        Map<String, String> attributes = mapParentAttributesFromStudent(user);
        attributes.put("role", UserDTO.Role.PARENT.toString()); // <- to może iść do metody mapParentAttributesFromStudent no nie?

        Map<String, List<String>> customAttributes = attributes.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> Collections.singletonList(e.getValue())));

        userRep.setAttributes(customAttributes);

        return userRep;
    }
    //                 tu nie mamy co przekazywać "role" jak jest już w "user" no nie ↓
    private static Map<String, String> mapUserAttributes(UserDTO user, UserDTO.Role role) {
        Map<String, String> userAttributes = new HashMap<>();
        CustomAttributesDTO customAttributes = user.getCustomAttributes();
        userAttributes.put("pesel", user.getCustomAttributes().getPesel());
        userAttributes.put("role", role.toString());
        customAttributes.getMiddleName().ifPresent(p -> userAttributes.put("middleName", p));
        customAttributes.getPhoneNumber().ifPresent(p -> userAttributes.put("phoneNumber", p));

        switch (role){
            // nie trzeba robić { jak nie ma potrzeby raczej
            case STUDENT: {
                customAttributes.getGroup().ifPresent(p -> userAttributes.put("group", p));
                customAttributes.getRelatedUser().ifPresent(p -> userAttributes.put("relatedUser", p));
                break;
            }
            case TEACHER:{
                if (customAttributes.getSubjects() != null && !customAttributes.getSubjects().isEmpty()) {
                    userAttributes.put("subjects", String.join(",", customAttributes.getSubjects()));
                }
                break;
            }
            case ADMIN:
                break;
            case PARENT:
                break;
        }

        return userAttributes;
    }

    private static Map<String, String> mapParentAttributesFromStudent(UserDTO user) {
        Map<String, String> userAttributes = new HashMap<>();
        userAttributes.put("pesel", "parent_" + user.getCustomAttributes().getPesel());

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

}
