package com.sms.usermanagementservice.control;  // ← jak widać package się zgadza z tym w UsersService
import com.sms.clients.KeycloakClient;
import com.sms.clients.entity.UserSearchParams;
import com.sms.usermanagement.CustomAttributesDTO;
import com.sms.usermanagement.UserDTO;
import com.sms.usermanagementservice.entity.User;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import java.util.*;
import java.util.stream.Stream;

import static com.sms.usermanagementservice.control.UserMapper.*;
import static org.junit.jupiter.api.Assertions.*;
public class UsersServiceTest {

    private final String username="username";
    private final String password="password";
    private final String firtsName="firstName";
    private final String lastName="lastName";
    private final String email="email";
    private final String middleName="middleName";
    private final String group="group";
    private final String student = "STUDENT";
    private final String pesel="pesel";
    private final String teacher = "TEACHER";
    private final String admin = "ADMIN";
    private final String id="sampleID";

    @Test
    void shouldFindUserByGroup(){
        UsersService service= new UsersService();
        List<UserRepresentation> users= new ArrayList<>();

        //CREATE USER
        UserRepresentation user= createStudentRep(username, password, firtsName, lastName, email, "IIIA", student, pesel, id, middleName);
        users.add(user);
        UserRepresentation user1= createStudentRep(username+"1", password+"1", firtsName+"1", lastName+"1", email+"1", "IIA", student, pesel+"1", id+"1", middleName+1);
        users.add(user1);

        //CREATE PARAMETERS MAP
        Map<String, String> map= new HashMap<>();
        map.put("group", "II");

        //ADD TO QUERY PARAMS
        QueryParams queryParams = new QueryParams(map);

        //FILTER USERS
        FilteredUsers filteredUsers = new FilteredUsers();
        List<UserDTO> list=filteredUsers.filterUsersByParam(users, queryParams);

        //SHOULD FIND TWO USERS AND THEY SHOULD BE IIIA AND IIA
        assertEquals(2, list.size());
        assertEquals( "IIIA", list.get(0).getCustomAttributes().getGroup().get());
        assertEquals( "IIA", list.get(1).getCustomAttributes().getGroup().get());
    }

    @Test
    void shouldFindUserByMiddleName(){
        UsersService service= new UsersService();
        List<UserRepresentation> users= new ArrayList<>();

        //CREATE USER
        UserRepresentation user= createStudentRep(username, password, firtsName, lastName, email, "IIIA", student, pesel, id, middleName);
        users.add(user);
        UserRepresentation user1= createStudentRep(username+"1", password+"1", firtsName+"1", lastName+"1", email+"1", "IIA", student, pesel+"1", id+"1", middleName+"1");
        users.add(user1);

        //CREATE PARAMETERS MAP
        Map<String, String> map= new HashMap<>();
        map.put("middleName", "1");

        //ADD TO QUERY PARAMS
        QueryParams queryParams = new QueryParams(map);

        //FILTER USERS
        FilteredUsers filteredUsers = new FilteredUsers();
        List<UserDTO> list=filteredUsers.filterUsersByParam(users, queryParams);

        //SHOULD FIND ONE USER AND HE SHOULD BE userName
        assertEquals(1, list.size());
        assertEquals( "middleName1", list.get(0).getCustomAttributes().getMiddleName().get());

        // \\ // \\ SHOULD FIND TWO USERS

        //CREATE PARAMETERS MAP
        Map<String, String> map1= new HashMap<>();
        map1.put("middleName", "middle");

        //ADD TO QUERY PARAMS
        QueryParams queryParams1 = new QueryParams(map1);

        //FILTER USERS
        FilteredUsers filteredUsers1 = new FilteredUsers();
        List<UserDTO> list1=filteredUsers1.filterUsersByParam(users, queryParams1);

        //SHOULD FIND ONE USER AND HE SHOULD BE middleName and middleName1
        assertEquals(2, list1.size());
        assertEquals( "middleName", list1.get(0).getCustomAttributes().getMiddleName().get());
        assertEquals( "middleName1", list1.get(1).getCustomAttributes().getMiddleName().get());

    }



    private UserRepresentation createStudentRep(String username, String password, String firstName, String lastName,
                                             String email, String group, String role, String pesel, String id, String middleName) {
        UserRepresentation userRep = new UserRepresentation();
        userRep.setEmail(email);
        userRep.setUsername(username);
        userRep.setFirstName(firstName);
        userRep.setLastName(lastName);
        userRep.setId(id);
        userRep.setCredentials(Collections.singletonList(getPasswordCredential(password)));

        Map<String, List<String>> customAttributes = new HashMap<>();
        customAttributes.put("role", Collections.singletonList(role));
        customAttributes.put("group", Collections.singletonList(group));
        customAttributes.put("pesel", Collections.singletonList(pesel));
        customAttributes.put("middleName", Collections.singletonList(middleName));

        userRep.setAttributes(customAttributes);
        return userRep;
    }

    private CredentialRepresentation getPasswordCredential(String password) {
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType("password");
        credential.setValue(password);
        credential.setTemporary(false);
        return credential;
    }
}



/*    @Test
    void shouldFindAttrib(){
        UsersService UserService=new UsersService();
        assertSame( UserService.compareAttrib("1a"), "group");
        assertSame( UserService.compareAttrib("3f"), "group");
        assertSame( UserService.compareAttrib("student"), "role");
        assertSame( UserService.compareAttrib("admin"), "role");
        assertSame( UserService.compareAttrib("teacher"), "role");
        assertSame( UserService.compareAttrib("parent"), "role");
        assertSame( UserService.compareAttrib("nazwisko"), "param");

         @Test
    void shouldFindAllUsers(){
        UsersService UserService=new UsersService();
        UserService.getUsers();
        assertTrue( UserService.userRepresentation.size()>2);
    }

    @Test
    void shouldFindSomeUsersByAttrib(){
        UsersService UserService=new UsersService();
        UserService.getByAttribute("STUDENT");
        assertSame(2, UserService.userRepresentation.size());
    }

    @Test
    void shouldFindUserBy2Attrib(){
        UsersService UserService=new UsersService();
        UserService.getByAttributes("STUDENT", "1a");
        assertSame(1, UserService.userRepresentation.size());
    }

    @Test
    void shouldFind1User(){
        UsersService UserService=new UsersService();
        UserService.getByAttributes("ADMIN", "1c");
        assertSame(1, UserService.userRepresentation.size());
    }

    @Test
    void shouldFindByFirstName(){
        UsersService UserService=new UsersService();
        UserService.getUser("Tomasz");
        assertSame(2, UserService.userRepresentation.size());
    }

    @Test
    void mainFunctionTest1(){
        UsersService UserService=new UsersService();
        UserService.match(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
        assertTrue( UserService.userRepresentation.size()>2);
    }

    @Test
    void mainFunctionTest2(){
        UsersService UserService=new UsersService();
        Optional<String> role= Optional.of("STUDENT");
        UserService.match(role, Optional.empty(), Optional.empty(), Optional.empty());
        assertSame(3, UserService.userRepresentation.size());
    }

    @Test
    void mainFunctionTest3(){
        UsersService UserService=new UsersService();
        Optional<String> role= Optional.of("STUDENT");
        Optional<String> group= Optional.of("1a");
        UserService.match(role, group, Optional.empty(), Optional.empty());
        assertSame(1, UserService.userRepresentation.size());
    }

    @Test
    void mainFunctionTest4(){
        UsersService UserService=new UsersService();
        Optional<String> role= Optional.of("STUDENT");
        Optional<String> group= Optional.of("1a");
        Optional<String> page= Optional.of("0");
        UserService.match(role, group, page, Optional.empty());
        assertSame(1, UserService.userRepresentation.size());
    }


    }*/