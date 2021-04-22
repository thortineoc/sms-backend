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

    // a tutaj piszemy testy do UsersService

    private final static KeycloakClient CLIENT = new KeycloakClient();
    private final static String TEST_USER_ID = "a43856df-96bf-4747-b947-0b2b127ae677";
    private final static String PREFIX = "INTEGRATION_TESTS_";
    private final static String KOPYTKO54_USER = PREFIX + "kopytko";


/*@AfterAll
    static void cleanup() {
        UserSearchParams params = new UserSearchParams().username(KOPYTKO54_USER);
        Optional<UserRepresentation> testUser = CLIENT.getUsers(params).stream().findFirst();
        testUser.ifPresent(userRepresentation -> CLIENT.deleteUser(userRepresentation.getId()));
    }
    */

    @Test
    void shouldFindSomeGroup(){
        UsersService service= new UsersService();

        //CREATE MAP OF PARAMS
        Map<String, String> map= new HashMap<>();
        map.put("group", "IIa");
        //
        assertEquals( 1, service.filterUserByParameters(map).size());
    }

    @Test
    void shouldFind3Users(){
        UsersService service= new UsersService();

        //CREATE MAP OF PARAMS
        Map<String, String> map= new HashMap<>();
        map.put("group", "IIa");
        //
        assertEquals( 3, service.getUsers().size());
    }

    @Test
    void userParamsTest(){
        UsersService service = new UsersService();

        //CREATE MAP OF PARAMS
        Map<String, String> map= new HashMap<>();
        map.put("group", "group");
        map.put("middleName", "middleName");
        map.put("pesel", "pesel");
        map.put("phoneNumber", "phoneNumber");
        //CREATE QUERY PARAMS
        QueryParams params=new QueryParams(map);
        assertEquals("group", params.getGroup());
        assertEquals("middleName", params.getMiddleName());
        assertEquals("pesel", params.getPesel());
        assertEquals("phoneNumber", params.getPhoneNumber());
    }

    @Test
    void userMapperTest(){
        UsersService service = new UsersService();

        //CREATE MAP OF PARAMS
        Map<String, String> map= new HashMap<>();
        map.put("group", "IIa");
        //CREATE QUERY PARAMS
        List<UserRepresentation> list = service.filterUserByParameters(map);
        assertEquals(1, list.size());

    }

    private UserRepresentation createUserRep(String username, String password, String firstName, String lastName,
                                             String email, String group, String role) {
        UserRepresentation userRep = new UserRepresentation();
        userRep.setEmail(email);
        userRep.setUsername(username);
        userRep.setFirstName(firstName);
        userRep.setLastName(lastName);
        userRep.setCredentials(Collections.singletonList(getPasswordCredential(password)));

        Map<String, List<String>> customAttributes = new HashMap<>();
        customAttributes.put("role", Collections.singletonList(role));
        customAttributes.put("group", Collections.singletonList(group));

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