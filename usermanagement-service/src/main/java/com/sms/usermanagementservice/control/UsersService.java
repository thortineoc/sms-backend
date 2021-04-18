package com.sms.usermanagementservice.control;


import com.sms.clients.KeycloakClient;
import com.sms.clients.entity.UserSearchParams;
import com.sms.usermanagement.UserDTO;
import com.sms.usermanagementservice.entity.User;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Pattern;

import static com.sms.usermanagementservice.control.UserMapper.toDTO;


@Component
@Scope("request")
public class UsersService {

    @Autowired
    private static KeycloakClient keycloakclient;

    private final static String SMSROLE = "role";
    private final static String SMSGROUP = "group";
    private final static String SMSPHONENUMBER = "phoneNumber";
    private final static String SMSSUBJECTS = "subjects";
    private final static String SMSPESEL = "pesel";
    private final static String SMSRELATED = "realatedUser";
    private final static String SMSMIDDLENAME = "middleName";

    public static void getUserById(String id) {
        Optional<UserRepresentation> user = keycloakclient.getUser(id);
        if (user.isPresent()) {
            UserRepresentation tmp=user.get();
            toDTO(buildUser(tmp));
        } else throw new IllegalStateException("User doesnt exist");
    }

    //search/object object->username/name/phone ip?
    public static void getUser(String object) {
        UserSearchParams params = new UserSearchParams().username(object);
        List<UserRepresentation> users = keycloakclient.getUsers(params);
        if (!users.isEmpty()) {
            for (UserRepresentation user : users) {
                toDTO(buildUser(user));
            }
        }else throw new IllegalStateException("Users not found!");
    }

    public void getUsers(int i){
        UserSearchParams params = new UserSearchParams().max(10).first(i*10);
        List<UserRepresentation> users = keycloakclient.getUsers(params);
        if(!users.isEmpty()){
            for (UserRepresentation user : users) {
                toDTO(buildUser(user));
            }
        }else throw new IllegalStateException("Users not found!");
    }

    public static void getString(String group) {
        List<UserRepresentation> users = keycloakclient.getAllUsers();
        if (!users.isEmpty()) {
            for (UserRepresentation user : users) {
                String tmpGroup= getSpecificAttrib(user.getAttributes(), SMSGROUP);
                if(tmpGroup.equals(group)) toDTO(buildUser(user));
            }
        } else throw new IllegalStateException("Users not found!");
    }

    public static void getRoleGroup(String object, String otherobject) {
        List<UserRepresentation> users = keycloakclient.getAllUsers();
        if (!users.isEmpty()) {
            String P1= compareAttrib(object);
            String P2= compareAttrib(otherobject);
            if(P1.equals(P2)) throw new IllegalArgumentException("Patters are equals");
            for (UserRepresentation user : users) {
                String attrib1=getSpecificAttrib(user.getAttributes(), P1);
                if(attrib1.equals(object.toLowerCase())){
                    String attrib2=getSpecificAttrib(user.getAttributes(), P2);
                    if(attrib2.equals(otherobject.toLowerCase())) toDTO(buildUser(user));
                }
            }
        } else throw new IllegalStateException("Users not found!");
    }

    private static User buildUser(UserRepresentation user) {
        return new User.Builder()
                .firstName(user.getFirstName())
                .username(user.getUsername())
                .role(getRole(user.getAttributes()))
                .userAttributes(MapUserAttributes(user.getAttributes()))
                .email(Optional.ofNullable(user.getEmail()))
                .build();
    }

    private static UserDTO.Role getRole(Map<String, List<String>> Attributes) {
        List<String> tmp = Attributes.get(SMSROLE);
        if (tmp.contains("student")) return UserDTO.Role.STUDENT;
        if (tmp.contains("teacher")) return UserDTO.Role.TEACHER;
        if (tmp.contains("parent")) return UserDTO.Role.PARENT;
        else
            return UserDTO.Role.ADMIN;
    }

    private static String getSpecificAttrib(Map<String, List<String>> Attributes, String SMS){
        return Attributes.get(SMS).stream().toString();
    }

    private static Map<String, String> MapUserAttributes(Map<String, List<String>> Attributes) {
        Map<String, String> userAttrib = new HashMap<>();
        userAttrib.put(SMSPESEL, getSpecificAttrib(Attributes, SMSPESEL));
        userAttrib.put(SMSPHONENUMBER, getSpecificAttrib(Attributes, SMSPHONENUMBER));
        userAttrib.put(SMSRELATED, getSpecificAttrib(Attributes, SMSRELATED));
        userAttrib.put(SMSMIDDLENAME, getSpecificAttrib(Attributes, SMSMIDDLENAME));
        userAttrib.put(SMSSUBJECTS, getSpecificAttrib(Attributes, SMSSUBJECTS));
        return userAttrib;
    }

    private static String compareAttrib(String object){

            String tmp=object.toLowerCase();
            boolean b= Pattern.matches("[1-9][a-z]}", tmp);
            if(b) return SMSGROUP;

            switch (tmp) {
                case "admin":
                case "teachers":
                case "students":
                case "parents":
                    return SMSROLE;
                default:
                    throw new IllegalArgumentException("pattern doesnt match");
            }
    }
}

/*
*     private String getGroup(Map<String, List<String>> Attributes) {
        return Attributes.get(SMSGROUP).stream().toString();
    }

    private String getPhoneNumber(Map<String, List<String>> Attributes) {
        return Attributes.get(SMSPHONENUMBER).stream().toString();
    }

    private String getSubject(Map<String, List<String>> Attributes) {
        return Attributes.get(SMSSUBJECTS).stream().toString();
    }

    private String getMiddleName(Map<String, List<String>> Attributes) {
        return Attributes.get(SMSMIDDLENAME).stream().toString();
    }

    private String getRelated(Map<String, List<String>> Attributes) {
        return Attributes.get(SMSRELATED).stream().toString();
    }

    private String getPesel(Map<String, List<String>> Attributes) {
        return Attributes.get(SMSPESEL).stream().toString();
    }
    */