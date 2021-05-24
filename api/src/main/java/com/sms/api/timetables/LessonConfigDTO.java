package com.sms.api.timetables;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.time.LocalTime;

@Value.Immutable
@Value.Style(builder = "new")
@JsonSerialize(as = ImmutableLessonConfigDTO.class)
@JsonDeserialize(as = ImmutableLessonConfigDTO.class, builder = ImmutableLessonConfigDTO.Builder.class)
public interface LessonConfigDTO {

    static ImmutableLessonConfigDTO.Builder builder() {
        return new ImmutableLessonConfigDTO.Builder();
    }

    LocalTime getStartTime();

    LocalTime getEndTime();
}
