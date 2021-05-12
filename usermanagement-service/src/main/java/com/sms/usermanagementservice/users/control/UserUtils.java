package com.sms.usermanagementservice.users.control;

import com.sms.api.usermanagement.UserDTO;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class UserUtils {

    private static final String ROLE = "role";

    public static boolean isRoleAndHasAttribute(UserRepresentation user, UserDTO.Role role, String attributeName, String attributeValue) {
        Map<String, List<String>> attributes = user.getAttributes();
        UserDTO.Role userRole = Optional.ofNullable(attributes.get(ROLE)).map(list -> list.stream()
                .findFirst()
                .orElseThrow(noRoleException(user)))
                .map(UserDTO.Role::valueOf)
                .orElseThrow(noRoleException(user));

        boolean hasAttribute = Optional.ofNullable(attributes.get(attributeName))
                .map(attribute -> attribute.contains(attributeValue))
                .orElse(false);

        return userRole.equals(role) && hasAttribute;
    }

    public static List<String> getFailedUserIds(Map<Boolean, List<UserRepresentation>> users) {
        if (users.getOrDefault(false, Collections.emptyList()).isEmpty()) {
            return Collections.emptyList();
        } else {
            return users.get(false).stream()
                    .map(UserRepresentation::getId)
                    .collect(Collectors.toList());
        }
    }

    private static Supplier<RuntimeException> noRoleException(UserRepresentation user) {
        return () -> new IllegalStateException("User: " + user.getId() + " does not have a role");
    }
}
