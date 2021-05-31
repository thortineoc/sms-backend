package com.sms.api.timetables;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@Value.Style(builder = "new")
@JsonSerialize(as = ImmutableSubjectInfoDTO.class)
@JsonDeserialize(as = ImmutableSubjectInfoDTO.class, builder = ImmutableSubjectInfoDTO.Builder.class)
public interface SubjectInfoDTO {

    static ImmutableSubjectInfoDTO.Builder builder() {
        return new ImmutableSubjectInfoDTO.Builder();
    }

    String getSubject();

    Integer getCount();
}
