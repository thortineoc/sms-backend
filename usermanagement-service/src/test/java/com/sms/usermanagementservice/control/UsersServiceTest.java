package com.sms.usermanagementservice.control;  // ← jak widać package się zgadza z tym w UsersService

import com.sms.clients.KeycloakClient;
import com.sms.clients.entity.UserSearchParams;
import com.sms.usermanagementservice.entity.CustomFilterParams;
import com.sms.usermanagementservice.entity.FilterParamsDTO;
import com.sms.usermanagementservice.entity.KeyCloakFilterParams;
import org.checkerframework.checker.nullness.qual.AssertNonNullIfNonNull;
import org.junit.jupiter.api.Test;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.springframework.test.util.AssertionErrors.assertEquals;



public class UsersServiceTest {


    private final static KeycloakClient CLIENT = new KeycloakClient();

    private final String USERNAME = "username";
    private final String PASSWORD = "password";
    private final String FIRSTNAME = "firstName";
    private final String LASTNAME = "lastName";
    private final String EMAIL = "email";
    private final String MIDDLENAME = "middleName";
    private final String GROUP = "group";
    private final String STUDENT = "STUDENT";
    private final String PESEL = "pesel";
    private final String TEACHER = "TEACHER";
    private final String ADMIN = "ADMIN";
    private final String ID = "sampleID";

    @Test
    void createUsers() {
        UserRepresentation user = createStudentRep(USERNAME, PASSWORD, FIRSTNAME, LASTNAME, EMAIL, MIDDLENAME,
                GROUP, STUDENT, PESEL, ID);
        CLIENT.createUser(user);
        UserRepresentation user1 = createStudentRep(USERNAME + "OLA", PASSWORD + "ALA", FIRSTNAME + "ALA",
                LASTNAME + "OLA", EMAIL + "OLA", MIDDLENAME + "OLA", GROUP + "ALA", STUDENT,  ID + "ALA", PESEL + "ALA");
        CLIENT.createUser(user1);
        UserRepresentation user2 = createStudentRep(USERNAME + "OLA", PASSWORD + "OLA", FIRSTNAME + "OLA",
                LASTNAME + "OLA", EMAIL + "OLA", MIDDLENAME + "OLA", GROUP + "OLA", STUDENT, ID + "OLA", PESEL + "OLA");
        CLIENT.createUser(user2);


        FilterParamsDTO filterParamsDTO = new FilterParamsDTO(
                Optional.empty(), //group
                Optional.empty(), //phone
                Optional.empty(), //middle
                Optional.of("pesel"), //pesel
                Optional.empty(), //first
                Optional.empty(), //last
                Optional.empty(), //email
                Optional.empty(), //username
                Optional.of("user")); //search


        KeyCloakFilterParams keyCloakFilterParams = UserMapper.mapKeyCloakFilterParams(filterParamsDTO);
        assertSame(keyCloakFilterParams.getFirstName().isPresent(), false);
        assertSame(keyCloakFilterParams.getLastName().isPresent(), false);
        assertSame(keyCloakFilterParams.getEmail().isPresent(), false);
        assertSame(keyCloakFilterParams.getUsername().isPresent(), false);
        assertSame(keyCloakFilterParams.getSearch().isPresent(), true);


        CustomFilterParams customFilterParams=UserMapper.mapCustomFilterParams(filterParamsDTO);
        assertSame(customFilterParams.getPhoneNumber().isPresent(), false);
        assertSame(customFilterParams.getGroup().isPresent(), false);
        assertSame(customFilterParams.getPesel().isPresent(), true);
        assertSame(customFilterParams.getMiddleName().isPresent(), false);


        UsersService usersService=new UsersService();
        UserFilteringService userFilteringService= new UserFilteringService();

        List<UserRepresentation> list = userFilteringService.keyCloakFilteringUsers(CLIENT, keyCloakFilterParams);
        assertSame(list.size(), 4);

        assertSame(userFilteringService.customFilteringUsers(list, customFilterParams).size(), 3);


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
