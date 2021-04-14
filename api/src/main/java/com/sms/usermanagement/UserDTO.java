package com.sms.usermanagement;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.util.Optional;

@Value.Immutable
@Value.Style(builder = "new")
@JsonSerialize(as = ImmutableUserDTO.class)
@JsonDeserialize(as = ImmutableUserDTO.class, builder = ImmutableUserDTO.Builder.class)
public interface UserDTO {

    static ImmutableUserDTO.Builder builder() {
        return new ImmutableUserDTO.Builder();
    }

    CustomAttributesDTO getCustomAttributes();

    String getId();

    String getUserName();

    String getFirstName();

    String getLastName();

    Role getRole();

    Optional<String> getEmail();

    enum Role {
        TEACHER,
        STUDENT,
        PARENT,
        ADMIN
    }
}