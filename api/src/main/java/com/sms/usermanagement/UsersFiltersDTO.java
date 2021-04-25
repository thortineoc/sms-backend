package com.sms.usermanagement;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.util.Optional;


@Value.Immutable
@Value.Style(builder = "new")
@JsonSerialize(as = ImmutableUsersFiltersDTO.class)
@JsonDeserialize(as = ImmutableUsersFiltersDTO.class, builder = ImmutableUsersFiltersDTO.Builder.class)
public interface UsersFiltersDTO {

    static ImmutableUsersFiltersDTO.Builder builder() { return new ImmutableUsersFiltersDTO.Builder();
    }

    Optional<String> getGroup();

    Optional<String> getPhoneNumber();

    Optional<String> getMiddleName();

    Optional<String> getPesel();

    Optional<String> getFirstName();

    Optional<String> getLastName();

    Optional<String> getEmail();

    Optional<String> getUsername();

    Optional<String> getSearch();

    Optional<String> getRole();


}
