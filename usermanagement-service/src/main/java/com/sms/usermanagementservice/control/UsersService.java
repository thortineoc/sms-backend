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

import static com.sms.usermanagementservice.control.UserMapper.toDTO;


@Component
@Scope("request")

public class UsersService {

    @Autowired
    private KeycloakClient keycloakclient;

    private List<UserDTO> DTOs = new ArrayList<>();
    private  Map<String, List<String>> Atributes;
    private final static String SMSROLE = "role";
    private final static String SMSGROUP = "group";
    private final static String SMSPHONENUMBER = "phoneNumber";
    private final static String SMSSUBJECTS = "subjects";
    private final static String SMSPESEL = "pesel";
    private final static String SMSRELATED = "realatedUser";
    private final static String SMSMIDDLENAME = "middleName";


    public  void getUserById(String id) {
        Optional<UserRepresentation> user = keycloakclient.getUser(id);
        if (user.isPresent()) {
            User USER = new User.Builder()
                    .firstName(user.get().getFirstName())
                    .lastName(user.get().getLastName())
                    .username(user.get().getUsername())
                    .role(UserDTO.Role.STUDENT)
                    .userAttributes(Collections.emptyMap()) //bez dodatkowych póki co
                    .email(Optional.ofNullable(user.get().getEmail()))
                    .build();
            toDTO(USER);
        }
    }

    public  void getGroup(String ID) {
        List<UserRepresentation> users = keycloakclient.getAllUsers();
        if (!users.isEmpty()) {
            for (UserRepresentation user : users) {
                Map<String, List<String>> UserAtribs=user.getAttributes();
                List<String> role = UserAtribs.get(SMSROLE);
                List<String> groupID= UserAtribs.get(SMSGROUP);
                for( String match : groupID){
                    if(match.equals(ID))
                        BuildUser(user, UserDTO.Role.valueOf(role.get(0)), groupID.get(0));
                }
            }
        }
    }


        public void getUser(String object){
            UserSearchParams params = new UserSearchParams();
            params.search(object);
            List<UserRepresentation> users = keycloakclient.getUsers(params);
            if (!users.isEmpty()) {
                for (UserRepresentation user : users) {
                    User USER = new User.Builder()
                            .firstName(user.getFirstName())
                            .username(user.getUsername())
                            .role(UserDTO.Role.STUDENT)
                            .userAttributes(Collections.emptyMap()) //bez dodatkowych póki co
                            .email(Optional.ofNullable(user.getEmail()))
                            .build();
                    DTOs.add(toDTO(USER));
                }
            }
        }


    public void getRole(String object){
        List<UserRepresentation> users = keycloakclient.getAllUsers();
        if (!users.isEmpty()) {
            for (UserRepresentation user : users) {
                Map<String, List<String>> UserAtribs=user.getAttributes();
                List<String> role = UserAtribs.get(SMSROLE);
                List<String> groupID= UserAtribs.get(SMSGROUP);
                for( String match : role){
                    if(match.equals(object))
                        BuildUser(user, UserDTO.Role.valueOf(object), groupID.get(0));
                }
            }
        }
    }

    private  void BuildUser(UserRepresentation user, UserDTO.Role ROLE, String GROUP){

        User USER = new User.Builder()
                .firstName(user.getFirstName())
                .username(user.getUsername())
                .role(ROLE)
                .userAttributes(Collections.emptyMap()) //bez dodatkowych póki co
                .email(Optional.ofNullable(user.getEmail()))
                .build();
        DTOs.add(toDTO(USER));
    }

    private UserDTO.Role getRole(Map<String, List<String>> Attributes){
        List<String> tmp=Attributes.get(SMSROLE);
        if(tmp.contains("STUDENT")) return UserDTO.Role.STUDENT;
        if(tmp.contains("TEACHER")) return UserDTO.Role.TEACHER;
        if(tmp.contains("PARENT"))  return UserDTO.Role.PARENT;
        else
            return UserDTO.Role.ADMIN;
    }

    private String getGroup(Map<String, List<String>> Attributes){
        return Attributes.get(SMSGROUP).get(0);
    }

    private String getPhoneNumber(Map<String, List<String>> Attributes){
        return Attributes.get(SMSPHONENUMBER).get(0);
    }

    private String getSubject(Map<String, List<String>> Attributes){
        return Attributes.get(SMSSUBJECTS).get(0);
    }

    private String getMiddleName(Map<String, List<String>> Attributes){
        return Attributes.get(SMSMIDDLENAME).get(0);
    }

    private String getRelated(Map<String, List<String>>Attributes){
        return Attributes.get(SMSRELATED).get(0);
    }
    private String getPesel(Map<String, List<String>> Attributes){
        return Attributes.get(SMSPESEL).get(0);
    }

    private Map<String, String> MapUserAttributes(Map<String, List<String>> Attributes){
        Map<String, String> userAttrib= new HashMap<>();
        userAttrib.put(SMSPESEL, getPesel(Attributes));
        userAttrib.put(SMSPHONENUMBER, getPhoneNumber(Attributes));
        userAttrib.put(SMSRELATED, getPesel(Attributes));
        userAttrib.put(SMSMIDDLENAME, getMiddleName(Attributes));
        userAttrib.put(SMSSUBJECTS, getSubject(Attributes));
        return userAttrib;
    }



    }

