package com.sms.usermanagementservice.users.control;

import com.sms.clients.KeycloakClient;
import com.sms.clients.entity.KcException;
import com.sms.clients.entity.KcResult;
import com.sms.clients.entity.UserSearchParams;
import com.sms.api.usermanagement.UserDTO;
import com.sms.usermanagementservice.users.entity.CustomFilterParams;
import com.sms.usermanagementservice.users.entity.KeyCloakFilterParams;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Collections;
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
                .filter(user -> filterFullString(parameters.getGroup(), "group", user))
                .filter(user -> filterSubstring(parameters.getMiddleName(), "middleName", user))
                .filter(user -> filterSubstring(parameters.getPesel(), "pesel", user))
                .filter(user -> filterSubstring(parameters.getPhoneNumber(), "phoneNumber", user))
                .filter(user -> filterFullString(parameters.getRole(), "role", user))
                .map(UserMapper::toDTO)
                .collect(Collectors.toList());
    }

    private boolean filterFullString(Optional<String> filterParam, String attributeName, UserRepresentation user) {
        return filterByAttr(true, filterParam, attributeName, user);
    }

    private boolean filterSubstring(Optional<String> filterParam, String attributeName, UserRepresentation user) {
        return filterByAttr(false, filterParam, attributeName, user);
    }

    private boolean filterByAttr(boolean fullString, Optional<String> filterParam, String attributeName, UserRepresentation user) {
        return filterParam.map(s -> Optional.ofNullable(user.getAttributes().get(attributeName))
                .map(list -> list.stream().findFirst()
                        .map(attr -> fullString
                                ? attr.equals(s)
                                : attr.contains(s))
                        .orElseThrow(() -> new IllegalStateException("Missing attribute (empty list saved in KC)")))
                .orElse(false))
                .orElse(true);
    }

    public List<UserRepresentation> filterByKCParams(KeyCloakFilterParams parameters) {

        UserSearchParams userSearchParams = new UserSearchParams();
        parameters.getEmail().ifPresent(userSearchParams::email);
        parameters.getUsername().ifPresent(userSearchParams::username);
        parameters.getLastName().ifPresent(userSearchParams::lastName);
        parameters.getFirstName().ifPresent(userSearchParams::firstName);
        parameters.getSearch().ifPresent(userSearchParams::search);

        KcResult<List<UserRepresentation>> result = keycloakClient.getUsers(userSearchParams);
        if (!result.isOk()) {
            throw new KcException(result, "Couldn't fetch users from keycloak");
        }
        return keycloakClient.getUsers(userSearchParams).getContent().orElse(Collections.emptyList());
    }


}
