package com.sms.usermanagement;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;


@Value.Immutable
@Value.Style(builder = "new")
@JsonSerialize(as = ImmutableGroupDTO.class)
@JsonDeserialize(as = ImmutableGroupDTO.class, builder = ImmutableGroupDTO.Builder.class)
public interface GroupDTO {

    static ImmutableGroupDTO.Builder builder() {
        return new ImmutableGroupDTO.Builder();
    }

    Integer getId();
    String getName();

}
