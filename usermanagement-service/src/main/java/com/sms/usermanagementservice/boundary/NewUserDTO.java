package com.sms.usermanagementservice.boundary;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.util.Set;

@Value.Immutable
@Value.Style(builder = "new")
@JsonSerialize(as = ImmutableNewUserDTO.class)
@JsonDeserialize(as = ImmutableNewUserDTO.class, builder = ImmutableNewUserDTO.Builder.class)
public interface NewUserDTO {

    static ImmutableNewUserDTO.Builder builder() {
        return new ImmutableNewUserDTO.Builder();
    }

    String getUserName();

    String getUserSurname();

    String getUserId();

    String getToken();

}
