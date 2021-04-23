package com.sms.usermanagementservice.control;

import com.sms.clients.KeycloakClient;
import com.sms.clients.entity.UserSearchParams;
import com.sms.usermanagement.UserDTO;
import com.sms.usermanagementservice.entity.CustomFilterParams;
import com.sms.usermanagementservice.entity.KeyCloakFilterParams;
import org.hibernate.query.criteria.internal.expression.function.AggregationFunction;
import org.keycloak.representations.idm.UserRepresentation;

import javax.validation.constraints.Max;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class UserFilteringService {

    public List<UserRepresentation> customFilteringUsers(List<UserRepresentation> users, CustomFilterParams parameters) {
        return users.stream()
                .filter(user -> filterByCustomAttribute(parameters.getGroup(), "group", user))
                .filter(user -> filterByCustomAttribute(parameters.getMiddleName(), "middleName", user))
                .filter(user -> filterByCustomAttribute(parameters.getPesel(), "pesel", user))
                .filter(user -> filterByCustomAttribute(parameters.getPhoneNumber(), "phoneNumber", user))
                .collect(Collectors.toList());
        //.map(UserMapper::toDTO)
    }

    public static boolean filterByCustomAttribute(Optional<String> filterParam, String attributeName, UserRepresentation user) {
        return filterParam.map(param -> (user.getAttributes().get(attributeName))
                .contains(param))
                .orElse(true);
    }

    public List<UserRepresentation> keyCloakFilteringUsers(KeycloakClient keycloakClient, KeyCloakFilterParams parameters) {

        UserSearchParams userSearchParams = new UserSearchParams();
        if (parameters.getEmail().isPresent()) userSearchParams.email(parameters.getEmail().get());
        if (parameters.getFirstName().isPresent()) userSearchParams.firstName(parameters.getFirstName().get());
        if (parameters.getLastName().isPresent()) userSearchParams.lastName(parameters.getLastName().get());
        if (parameters.getUsername().isPresent()) userSearchParams.username(parameters.getUsername().get());
        if (parameters.getSearch().isPresent()) userSearchParams.search(parameters.getSearch().get());

        userSearchParams.max(Integer.MAX_VALUE);
        return keycloakClient.getUsers(userSearchParams);
    }


}
