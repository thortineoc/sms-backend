package com.sms.usermanagementservice.control;

import com.sms.clients.KeycloakClient;
import com.sms.clients.entity.UserSearchParams;
import com.sms.usermanagement.UserDTO;
import com.sms.usermanagementservice.entity.CustomFilterParams;
import com.sms.usermanagementservice.entity.KeyCloakFilterParams;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Scope("request")
public class UserFilteringService {


    @Autowired
    KeycloakClient keycloakClient;

    public List<UserDTO> customFilteringUsers(List<UserRepresentation> users, CustomFilterParams parameters) {
        return users.stream()
                .filter(user -> filterByCustomAttribute(parameters.getGroup(), "group", user))
                .filter(user -> filterByCustomAttribute(parameters.getMiddleName(), "middleName", user))
                .filter(user -> filterByCustomAttribute(parameters.getPesel(), "pesel", user))
                .filter(user -> filterByCustomAttribute(parameters.getPhoneNumber(), "phoneNumber", user))
                .filter(user -> filterByCustomAttribute(parameters.getRole(), "role", user))
                .map(UserMapper::toDTO)
                .collect(Collectors.toList());


    }

    private boolean filterByCustomAttribute(Optional<String> filterParam, String attributeName, UserRepresentation user) {
        return filterParam.map(s -> Optional.ofNullable(user.getAttributes().get(attributeName))
                .flatMap(list -> list.stream().findFirst())
                .map(attr -> attr.contains(s))
                .orElseThrow(() -> new IllegalStateException("Illegal parameter")))
                .orElse(true);
    }

    public List<UserRepresentation> keyCloakFilteringUsers(KeyCloakFilterParams parameters) {

        UserSearchParams userSearchParams = new UserSearchParams();
        parameters.getEmail().ifPresent(userSearchParams::email);
        parameters.getUsername().ifPresent(userSearchParams::username);
        parameters.getLastName().ifPresent(userSearchParams::lastName);
        parameters.getFirstName().ifPresent(userSearchParams::firstName);
        parameters.getSearch().ifPresent(userSearchParams::search);

        return keycloakClient.getUsers(userSearchParams);
    }


}
