package com.sms.api.homework;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@Value.Style(builder = "new")
@JsonSerialize(as = ImmutableFileLinkDTO.class)
@JsonDeserialize(as = ImmutableFileLinkDTO.class, builder = ImmutableFileLinkDTO.Builder.class)
public interface FileLinkDTO {

    static ImmutableFileLinkDTO.Builder builder() {
        return new ImmutableFileLinkDTO.Builder();
    }

    Long getId();

    Long getSize();

    String getFilename();

    String getUri();

    enum Type {
        HOMEWORK, ANSWER, PROFILE
    }
}
