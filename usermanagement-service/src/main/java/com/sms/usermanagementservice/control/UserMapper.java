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

    public static UserDTO toDTO(UserRepresentation user){
        return UserDTO.builder()
                .id(user.getId())
                .pesel(user.getAttributes().get("pesel").toString())
                .userName(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(UserDTO.Role.valueOf(user.getAttributes().get("role").get(0)))
                .email(user.getEmail())
                .customAttributes(mapUserAttributes_(mapUserAttributes(user)))
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

    public static User toUser(UserRepresentation user) {
        return   User.builder()
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .userAttributes(mapUserAttributes(user))
                .email(Optional.ofNullable(user.getEmail()))
                .role(UserDTO.Role.valueOf(user.getAttributes().get("role").get(0)))
                .build();
    }


    public static Map<String, String> mapUserAttributes(UserRepresentation user) {
        Map<String, List<String>> customAttributes = user.getAttributes();

        if(customAttributes.containsKey("subjects")){
            List<String> list = Collections.singletonList(String.join("," , customAttributes.get("subjects")));
            customAttributes.replace("subjects", list);
        }
        return customAttributes.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().stream().findFirst().get()));
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

    //if attributes.get("subjects/related") doesnt exist -> nullptr { teacher != related, student != subjects}
    public static CustomAttributesDTO mapUserAttributes(Map<String, String> attributes) {
        return CustomAttributesDTO.builder()
                .group(Optional.ofNullable(attributes.get("group")))
                .middleName(Optional.ofNullable(attributes.get("middleName")))
                .phoneNumber(Optional.ofNullable(attributes.get("phoneNumber")))
                .subjects(Arrays.asList(attributes.get("subjects").split(",")))
                .relatedUser(Optional.ofNullable(attributes.get("relatedUser")))
                .build();
    }


    public static CustomAttributesDTO mapUserAttributes_(Map<String, String> attributes) {
        switch(attributes.get("role")) {
            case "ADMIN": //custom attributes for admin???
            case "STUDENT":
                return CustomAttributesDTO.builder()
                        .group(Optional.ofNullable(attributes.get("group")))
                        .middleName(Optional.ofNullable(attributes.get("middleName")))
                        .phoneNumber(Optional.ofNullable(attributes.get("phoneNumber")))
                        .relatedUser(Optional.ofNullable(attributes.get("relatedUser")))
                        .build();
            case "TEACHER":
                return CustomAttributesDTO.builder()
                        .middleName(Optional.ofNullable(attributes.get("middleName")))
                        .phoneNumber(Optional.ofNullable(attributes.get("phoneNumber")))
                        .subjects(Arrays.asList(attributes.get("subjects").split(",")))
                        .relatedUser(Optional.ofNullable(attributes.get("relatedUser")))
                        .build();
            case "PARENT":
                return CustomAttributesDTO.builder()
                    .middleName(Optional.ofNullable(attributes.get("middleName")))
                    .phoneNumber(Optional.ofNullable(attributes.get("phoneNumber")))
                    .relatedUser(Optional.ofNullable(attributes.get("relatedUser")))
                    .build();
            default: throw new IllegalStateException("Illegal role");
        }
    }

    private static Map<String, List<String>> asMultimap(Map<String, String> map) {
        return map.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> Collections.singletonList(e.getValue())));
    }
}
