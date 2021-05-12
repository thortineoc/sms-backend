package com.sms.api.usermanagement;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.util.List;
import java.util.Optional;

@Value.Immutable
@Value.Style(builder = "new")
@JsonSerialize(as = ImmutableCustomAttributesDTO.class)
@JsonDeserialize(as = ImmutableCustomAttributesDTO.class, builder = ImmutableCustomAttributesDTO.Builder.class)
public interface CustomAttributesDTO {

    static ImmutableCustomAttributesDTO.Builder builder() {
        return new ImmutableCustomAttributesDTO.Builder();
    }

    Optional<String> getGroup();

    List<String> getSubjects();

    Optional<String> getPhoneNumber();

    Optional<String> getMiddleName();

    Optional<String> getRelatedUser();
}
