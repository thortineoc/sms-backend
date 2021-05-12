package com.sms.usermanagementservice.users.control;


import com.sms.api.usermanagement.CustomAttributesDTO;
import com.sms.api.usermanagement.ImmutableCustomAttributesDTO;
import com.sms.api.usermanagement.UserDTO;
import com.sms.api.usermanagement.UsersFiltersDTO;
import com.sms.usermanagementservice.users.entity.CustomFilterParams;
import com.sms.usermanagementservice.users.entity.KeyCloakFilterParams;
import com.sms.usermanagementservice.users.entity.User;
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
                .pesel(mapPesel(user))
                .userName(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(UserDTO.Role.valueOf(user.getAttributes().get("role").get(0)))
                .email(Optional.ofNullable(user.getEmail()))
                .customAttributes(mapToUserAttributes(mapUserRepresentation(user)))
                .build();
    }

    private static String mapPesel(UserRepresentation userRep) {
        return Optional.ofNullable(userRep.getAttributes().get("pesel"))
                .flatMap(list -> list.stream().findFirst())
                .orElseThrow(() -> new IllegalStateException("peselu nie ma"));
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

    public static KeyCloakFilterParams mapKeyCloakFilterParams(UsersFiltersDTO filterParamsDTO) {
        return KeyCloakFilterParams.builder()
                .firstName(filterParamsDTO.getFirstName())
                .lastName(filterParamsDTO.getLastName())
                .search(filterParamsDTO.getSearch())
                .username(filterParamsDTO.getUsername())
                .email(filterParamsDTO.getEmail())
                .build();
    }

    public static CustomFilterParams mapCustomFilterParams(UsersFiltersDTO filterParamsDTO) {
        return CustomFilterParams.builder()
                .pesel(filterParamsDTO.getPesel())
                .phoneNumber(filterParamsDTO.getPhoneNumber())
                .group(filterParamsDTO.getGroup())
                .middleName(filterParamsDTO.getMiddleName())
                .role(filterParamsDTO.getRole())
                .build();
    }

    public static Map<String, String> mapUserRepresentation(UserRepresentation user) {
        return user.getAttributes().entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> "subjects".equals(entry.getKey())
                                ? String.join(",", entry.getValue())
                                : entry.getValue().stream()
                                .findFirst()
                                .orElseThrow(() -> new IllegalStateException("Missing parameter value"))
                ));
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

    public static CustomAttributesDTO mapUserAttributes(Map<String, String> attributes) {
        return CustomAttributesDTO.builder()
                .group(Optional.ofNullable(attributes.get("group")))
                .middleName(Optional.ofNullable(attributes.get("middleName")))
                .phoneNumber(Optional.ofNullable(attributes.get("phoneNumber")))
                .subjects(mapSubjects(attributes))
                .relatedUser(Optional.ofNullable(attributes.get("relatedUser")))
                .build();
    }

    public static CustomAttributesDTO mapToUserAttributes(Map<String, String> attributes) {
        ImmutableCustomAttributesDTO.Builder builder = CustomAttributesDTO.builder();
        Optional.ofNullable(attributes.get("group")).ifPresent(builder::group);
        Optional.ofNullable(attributes.get("middleName")).ifPresent(builder::middleName);
        Optional.ofNullable(attributes.get("phoneNumber")).ifPresent(builder::phoneNumber);
        Optional.ofNullable(attributes.get("relatedUser")).ifPresent(builder::relatedUser);
        Optional.ofNullable(attributes.get("subjects")).map(s -> s.split(","))
                .map(Arrays::asList)
                .ifPresent(builder::subjects);

        return builder.build();
    }

    private static List<String> mapSubjects(Map<String, String> attributes) {
        return Optional.ofNullable(attributes.get("subjects")).map(s -> s.split(","))
                .map(Arrays::asList)
                .orElse(Collections.emptyList());
    }

    private static Map<String, List<String>> asMultimap(Map<String, String> map) {
        return map.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> Collections.singletonList(e.getValue())));
    }
}
