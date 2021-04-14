package com.sms.authlib;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.util.Set;

@Value.Immutable
@Value.Style(builder = "new")
@JsonSerialize(as = ImmutableUserDTO.class)
@JsonDeserialize(as = ImmutableUserDTO.class, builder = ImmutableUserDTO.Builder.class)
public interface UserDTO {

    static ImmutableUserDTO.Builder builder() {
        return new ImmutableUserDTO.Builder();
    }

    String getUserName();

    String getFirstName();

    String getLastName();

    Set<String> getRoles();

    String getEmail();

    String getGroup();

}