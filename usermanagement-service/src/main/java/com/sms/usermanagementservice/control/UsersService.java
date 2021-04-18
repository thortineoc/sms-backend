package com.sms.usermanagementservice.control;

import com.sms.clients.KeycloakClient;
import com.sms.clients.entity.UserSearchParams;
import com.sms.usermanagement.UserDTO;
import com.sms.usermanagementservice.entity.User;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import javax.ws.rs.BadRequestException;
import java.awt.geom.IllegalPathStateException;
import java.util.*;
import java.util.regex.Pattern;

@Component
@Scope("request")
public class UsersService {

    private final KeycloakClient keycloakClient=new KeycloakClient();
    public List<UserRepresentation> userRepresentation=new ArrayList<>();
    private final static String SMSROLE = "role";
    private final static String SMSGROUP = "group";
    private final static String SMSPHONENUMBER = "phoneNumber";
    private final static String SMSSUBJECTS = "subjects";
    private final static String SMSPESEL = "pesel";
    private final static String SMSRELATED = "realatedUser";
    private final static String SMSMIDDLENAME = "middleName";

    //object object->username/name/last/mail?
     public void getUser(String param) {
        UserSearchParams params = new UserSearchParams().username(param);
        List<UserRepresentation> users = keycloakClient.getUsers(params);

        if(users.isEmpty()){
            params=new UserSearchParams().firstName(param);
            users = keycloakClient.getUsers(params);
        }
        if(users.isEmpty()){
             params=new UserSearchParams().lastName(param);
             users = keycloakClient.getUsers(params);
        }
        if(users.isEmpty()){
             params=new UserSearchParams().email(param);
             users = keycloakClient.getUsers(params);
        }
        if(users.isEmpty()) {
             Optional<UserRepresentation> user = keycloakClient.getUser(param);
             if(user.isPresent()) users.add(user.get());
        }
        if (!users.isEmpty()) {
            userRepresentation.addAll(users);
        }else throw new IllegalStateException("Users not found!");
    }

    public void getUsers(){
        List<UserRepresentation> users = keycloakClient.getAllUsers();
        if(!users.isEmpty()){
            userRepresentation.addAll(users);
        }else throw new IllegalStateException("Users not found!");
    }

    public void getByAttributes(String param1, String param2) {
        List<UserRepresentation> users = keycloakClient.getAllUsers();
        if (!users.isEmpty()) {
            String P1= compareAttrib(param1);
            String P2= compareAttrib(param2);
            if(P1.equals(P2)) throw new IllegalArgumentException("Patterns are equal");
            for (UserRepresentation user : users) {
                String attrib1=getSpecificAttrib(user.getAttributes(), P1);
                if(attrib1.equals(param1)){
                    String attrib2=getSpecificAttrib(user.getAttributes(), P2);
                    if(attrib2.equals(param2)) userRepresentation.add(user);
                }
            }
        } else throw new IllegalStateException("Users not found!");
    }

    public void getByAttribute(String param1){
        List<UserRepresentation> users = keycloakClient.getAllUsers();
        if (!users.isEmpty()) {
            String P1= compareAttrib(param1);
          //  if(param1.equals(SMSROLE))
            for(UserRepresentation user : users){
                String attrib1=getSpecificAttrib(user.getAttributes(), P1);
                if(attrib1.equals(param1)) userRepresentation.add(user);
            }
        }
    }

    public void match(Optional<String> par1, Optional<String> par2, Optional<String> par3, Optional<String> par4){
        if(par1.isPresent()){
            if(par2.isPresent()){
                if(par3.isPresent()){
                    if(par4.isPresent())
                        fourAttributes(par1.get(), par2.get(), par3.get(), par4.get());
                    else threeAttributes(par1.get(), par2.get(), par3.get());
                }else twoAttributes(par1.get(), par2.get());
            }else oneAttribute(par1.get());
        }else getUsers();
    }

    private void divideToPages(String pages){
        int onPage=Integer.parseInt(pages);
        if(onPage!=0){
            if(userRepresentation.size()>(onPage+1)*10)
                 userRepresentation=userRepresentation.subList(onPage*10, (onPage+1)*10);
            else throw new BadRequestException("Not enough users");
        }
     }

    private void sort(String sort){
    //TODO
    }

    private void oneAttribute(String param){
        String val1=compareAttrib(param);
        switch (val1) {
            case SMSROLE:
            case SMSGROUP:
                getByAttribute(param);
                break;
            case "SORT":
                sort(param);
                break;
            case "PAGE":
                divideToPages(val1);
                break;
            case "param":
                getUser(param);
                break;
            default:
                throw new IllegalPathStateException("Illegal path");
        }
    }

    private void twoAttributes(String param1, String param2){
        String val1 = compareAttrib(param1);
        String val2 = compareAttrib(param2);
        if((val1.equals(SMSROLE) && val2.equals(SMSGROUP) ) || (val2.equals(SMSROLE) && val1.equals(SMSGROUP))) {
            getByAttributes(param1, param2);
        }else if((val1.equals(SMSROLE) || val1.equals(SMSGROUP)) && val2.equals("SORT")){
            getByAttribute(param1);
            sort(param2);
        }else if((val1.equals(SMSROLE) || val1.equals(SMSGROUP)) && (val2.equals("PAGE") )){
            getByAttribute(param1);
            divideToPages(param2);
        }else if(val1.equals("PAGE") && val2.equals("SORT")){
            divideToPages(param1);
            sort(param2);
        }else if(val2.equals("SORT")) {
            getUser(param1);
            sort(param2);
        }else if(val2.equals("PAGE")) {
            getUser(param1);
            divideToPages(param2);
        }else throw new IllegalPathStateException("Illegal path");
    }

    private void threeAttributes(String param1, String param2, String param3){
        String val1=compareAttrib(param1);
        String val2=compareAttrib(param2);
        String val3=compareAttrib(param3);
        if((val1.equals(SMSROLE) || val1.equals(SMSGROUP)) && val2.equals("PAGE") && val3.equals("SORT") ){
            getByAttribute(param1);
            divideToPages(param2);
            sort(param3);
        }else if(val1.equals("PARAM") && val2.equals("PAGE") && val3.equals("SORT") ){
            getUser(param1);
            divideToPages(param2);
            sort(param3);
        }else if((val1.equals(SMSROLE) || val1.equals(SMSGROUP)) && (val2.equals(SMSROLE) || val2.equals(SMSGROUP)) && val3.equals("PAGE")){
            getByAttributes(param1, param2);
            divideToPages(param3);
        } else if((val1.equals(SMSROLE) || val1.equals(SMSGROUP)) && (val2.equals(SMSROLE) || val2.equals(SMSGROUP)) && val3.equals("SORT")){
            getByAttributes(param1, param2);
            sort(param3);
        }else throw new IllegalPathStateException("Illegal path");
    }

    private void fourAttributes(String param1, String param2, String param3, String param4){
        String val1=compareAttrib(param1);
        String val2=compareAttrib(param2);
        String val3=compareAttrib(param3);
        String val4=compareAttrib(param4);

        if((val1.equals(SMSGROUP) || val1.equals(SMSROLE)) && (val2.equals(SMSGROUP) || val2.equals(SMSROLE)) &&
                val3.equals("PAGE") && val4.equals("SORT")){
            getByAttributes(param1, param2);
            divideToPages(param3);
            sort(param4);
        }else if((val1.equals(SMSGROUP) || val1.equals(SMSROLE)) && val2.equals("param") && val3.equals("PAGE") && val4.equals("SORT")){
            getByAttribute(param1);
            getUser(param2);
            divideToPages(param3);
            sort(param4);
        }else throw new IllegalPathStateException("Illegal path");
    }

    public  String compareAttrib(String object){
        String tmp=object.toLowerCase();
        boolean b= Pattern.matches("[1-9][a-z]", tmp);
        if(b) return SMSGROUP;
        b= Pattern.matches("[0-9]", tmp);
        if(b) return "PAGE";
        if(tmp.equals("+") || tmp.equals("-")) return "SORT";
        switch (tmp) {
            case "admin":
            case "teacher":
            case "student":
            case "parent":
                return SMSROLE;
            default:
                return "param";
        }
    }

    private  UserDTO.Role Role(Map<String, List<String>> Attributes) {
        List<String> tmp = Attributes.get(SMSROLE);
        if (tmp.contains("STUDENT")) return UserDTO.Role.STUDENT;
        if (tmp.contains("TEACHER")) return UserDTO.Role.TEACHER;
        if (tmp.contains("PARENT")) return UserDTO.Role.PARENT;
        else
            return UserDTO.Role.ADMIN;
    }

    public  String getSpecificAttrib(Map<String, List<String>> Attributes, String SMS){
        return Attributes.get(SMS).get(0);
    }

    private  Map<String, String> MapUserAttributes(Map<String, List<String>> Attributes) {
        Map<String, String> userAttributes = new HashMap<>();
        userAttributes.put(SMSPESEL, getSpecificAttrib(Attributes, SMSPESEL));
        userAttributes.put(SMSPHONENUMBER, getSpecificAttrib(Attributes, SMSPHONENUMBER));
        userAttributes.put(SMSRELATED, getSpecificAttrib(Attributes, SMSRELATED));
        userAttributes.put(SMSMIDDLENAME, getSpecificAttrib(Attributes, SMSMIDDLENAME));
        userAttributes.put(SMSSUBJECTS, getSpecificAttrib(Attributes, SMSSUBJECTS));
        return userAttributes;
    }

    private  User buildUser(UserRepresentation user) {
        return new User.Builder()
                 .firstName(user.getFirstName())
                 .username(user.getUsername())
                 .role(Role(user.getAttributes()))
                 .userAttributes(MapUserAttributes(user.getAttributes()))
                 .email(Optional.ofNullable(user.getEmail()))
                 .build();
    }



    public void createStudentWithParent(UserDTO user) {
        createUser(user);

        UserSearchParams params = new UserSearchParams().username(calculateUsername(user));
        UserRepresentation createdStudent = keycloakClient.getUsers(params)
                .stream().findFirst().orElseThrow(() -> new IllegalStateException("User was not created"));

        createParent(user, createdStudent);
    }

    public void createUser(UserDTO user) {
        UserRepresentation userRep = UserMapper
                .toUserRepresentation(user, calculateUsername(user), calculatePassword(user));

        if (!keycloakClient.createUser(userRep)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
    }

    private void createParent(UserDTO user, UserRepresentation createdStudent) {

        UserRepresentation parent = UserMapper
                .toParentRepresentationFromStudent(user, calculateUsername(user), calculatePassword(user));
        Map<String, List<String>> parentAttributes = new HashMap<>(parent.getAttributes());
        parentAttributes.put("relatedUser", Collections.singletonList(createdStudent.getId()));
        parent.setAttributes(parentAttributes);


        if (!keycloakClient.createUser(parent)) {
            keycloakClient.deleteUser(createdStudent.getId());
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }

        updateStudentRelatedUser(createdStudent, calculateUsername(user));
    }

    private void updateStudentRelatedUser(UserRepresentation createdStudent, String parentUsername) {

        UserSearchParams params = new UserSearchParams().username(parentUsername);
        UserRepresentation createdParent = keycloakClient.getUsers(params)
                .stream().findFirst().orElseThrow(() -> new IllegalStateException("User was not created"));

        Map<String, List<String>> studentAttributes = new HashMap<>(createdStudent.getAttributes());
        studentAttributes.put("relatedUser", Collections.singletonList(createdParent.getId()));
        createdStudent.setAttributes(studentAttributes);

        if (!keycloakClient.updateUser(createdStudent.getId(), createdStudent)) {
            keycloakClient.deleteUser(createdStudent.getId());
            keycloakClient.deleteUser(createdParent.getId());
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
    }

    private String calculatePassword(UserDTO user) {
        return user.getFirstName().substring(0, Math.min(user.getFirstName().length(), 4)) +
                user.getLastName().substring(0, Math.min(user.getLastName().length(), 4));
    }

    private String calculateUsername(UserDTO user) {
       // switch (user.getRole()) {
       //     case STUDENT: return "s_" + user.getPesel();
       //     case ADMIN: return "a_" + user.getPesel();
       //     case TEACHER: return "t_" + user.getPesel();
       //     case PARENT: return "p_" + user.getPesel();
       //     default: throw new IllegalStateException();
        // }
        return user.getUserName();
    }
}


/*
     private String getGroup(Map<String, List<String>> Attributes) {
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

 public void getUserById(String id) {
        Optional<UserRepresentation> user = keycloakClient.getUser(id);
        List<User> userList=new ArrayList<>();
        if (user.isPresent()) {
            UserRepresentation tmp=user.get();
            userRepresentation.add(tmp);
        }else throw new IllegalStateException("User doesnt exist");
    }


    */



