package com.sms.api.authlib;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@Value.Style(builder = "new")
@JsonSerialize(as = ImmutableTokenDTO.class)
@JsonDeserialize(as = ImmutableTokenDTO.class, builder = ImmutableTokenDTO.Builder.class)
public interface TokenDTO {

    static ImmutableTokenDTO.Builder builder() {
        return new ImmutableTokenDTO.Builder();
    }

    @JsonProperty("access_token")
    String getAccessToken();

    @JsonProperty("expires_in")
    Integer getExpiration();

    @JsonProperty("refresh_expires_in")
    Integer getRefreshExpiration();

    @JsonProperty("refresh_token")
    String getRefreshToken();

    @JsonProperty("token_type")
    String getTokenType();

    @JsonProperty("not-before-policy")
    Integer getNotBeforePolicy();

    @JsonProperty("session_state")
    String getSessionState();

    @JsonProperty("scope")
    String getScope();

}