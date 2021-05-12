package com.sms.usermanagementservice.users.control;

import com.sms.api.usermanagement.UserDTO;
import com.sms.api.usermanagement.UsersFiltersDTO;
import com.sms.usermanagementservice.users.entity.CustomFilterParams;
import com.sms.usermanagementservice.users.entity.KeyCloakFilterParams;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.*;

class UsersServiceTest {

    @Test
    void createAndFindAllUsers() {
        List<UserRepresentation> userList = createSomeUsers();
        //CREATE USERSFILTERS
        UsersFiltersDTO filters = UsersFiltersDTO.builder()
                .build();
        //CREATE KEYCLOAK FILTERS
        KeyCloakFilterParams keyCloakFilterParams = UserMapper.mapKeyCloakFilterParams(filters);
        CustomFilterParams customFilterParams = UserMapper.mapCustomFilterParams(filters);
        //FIND USERS
        UserFilteringService userFilteringService = new UserFilteringService();
        List<UserDTO> userDTOList = userFilteringService.customFilteringUsers(userList, customFilterParams);
        //SHOULD FIND 3
        Assertions.assertEquals(3, userDTOList.size());
    }

    @Test
    void createAndFindUserByRole() {
        List<UserRepresentation> userList = createSomeUsers();
        //CREATE USERSFILTERS
        UsersFiltersDTO filters = UsersFiltersDTO.builder()
                .role("STUDENT")
                .build();
        //CREATE KEYCLOAK FILTERS
        KeyCloakFilterParams keyCloakFilterParams = UserMapper.mapKeyCloakFilterParams(filters);
        CustomFilterParams customFilterParams = UserMapper.mapCustomFilterParams(filters);
        //FIND USERS
        UserFilteringService userFilteringService = new UserFilteringService();
        List<UserDTO> userDTOList = userFilteringService.customFilteringUsers(userList, customFilterParams);
        //SHOULD FIND 3
        Assertions.assertEquals(2, userDTOList.size());
    }

    @Test
    void createAndFindUserByRoleAndMiddleName() {
        List<UserRepresentation> userList = createSomeUsers();
        //CREATE USERSFILTERS
        UsersFiltersDTO filters = UsersFiltersDTO.builder()
                .role("STUDENT")
                .middleName("OT")
                .build();
        //CREATE KEYCLOAK FILTERS
        KeyCloakFilterParams keyCloakFilterParams = UserMapper.mapKeyCloakFilterParams(filters);
        CustomFilterParams customFilterParams = UserMapper.mapCustomFilterParams(filters);
        //FIND USERS
        UserFilteringService userFilteringService = new UserFilteringService();
        List<UserDTO> userDTOList = userFilteringService.customFilteringUsers(userList, customFilterParams);
        //SHOULD FIND 3
        Assertions.assertEquals(1, userDTOList.size());
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

    private List<UserRepresentation> createSomeUsers() {
        List<UserRepresentation> usersList = new ArrayList<>();
        String PASSWORD = "password";
        String USERNAME = "username";
        String FIRSTNAME = "firstName";
        String LASTNAME = "lastName";
        String EMAIL = "email";
        String MIDDLENAME = "middleName";
        String GROUP = "group";
        String PESEL = "pesel";
        String ADMIN = "ADMIN";
        String ID = "sampleID";
        usersList.add(createStudentRep(
                USERNAME + "1",
                PASSWORD + "1",
                FIRSTNAME + "1",
                LASTNAME + "1",
                EMAIL + "1",
                GROUP + "1",
                ADMIN,
                PESEL + "1",
                ID + "1",
                MIDDLENAME + "1"
        ));
        String STUDENT = "STUDENT";
        usersList.add(createStudentRep(
                USERNAME + "KOT",
                PASSWORD + "KOT",
                FIRSTNAME + "KOT",
                LASTNAME + "KOT",
                EMAIL + "KOT",
                GROUP + "KOT",
                STUDENT,
                PESEL + "KOT",
                ID + "KOT",
                MIDDLENAME + "KOT"
        ));
        usersList.add(createStudentRep(
                USERNAME,
                PASSWORD,
                FIRSTNAME,
                LASTNAME,
                EMAIL,
                GROUP,
                STUDENT,
                PESEL,
                ID,
                MIDDLENAME
        ));

        return usersList;
    }
}
