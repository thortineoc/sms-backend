package com.sms.usermanagementservice.control;  // ← jak widać package się zgadza z tym w UsersService

import com.sms.clients.KeycloakClient;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



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
