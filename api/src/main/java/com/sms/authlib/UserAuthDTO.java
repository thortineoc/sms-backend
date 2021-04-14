package com.sms.authlib;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.util.Set;

@Value.Immutable
@Value.Style(builder = "new")
@JsonSerialize(as = ImmutableUserAuthDTO.class)
@JsonDeserialize(as = ImmutableUserAuthDTO.class, builder = ImmutableUserAuthDTO.Builder.class)
public interface UserAuthDTO {

    static ImmutableUserAuthDTO.Builder builder() {
        return new ImmutableUserAuthDTO.Builder();
    }

    String getUserName();

    String getUserId();

    String getToken();

    Set<String> getRoles();

}
