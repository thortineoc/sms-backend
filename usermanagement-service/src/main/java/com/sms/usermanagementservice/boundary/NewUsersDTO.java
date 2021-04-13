package com.sms.usermanagementservice.boundary;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.util.List;
import java.util.Set;

@Value.Immutable
@Value.Style(builder = "new")
@JsonSerialize(as = ImmutableNewUsersDTO.class)
@JsonDeserialize(as = ImmutableNewUsersDTO.class, builder = ImmutableNewUsersDTO.Builder.class)

public interface NewUsersDTO {

    static ImmutableNewUsersDTO.Builder builder() {
        return new ImmutableNewUsersDTO.Builder();
    }

    List<NewUserDTO> getAllUsers();

}
