package com.sms.api.timetables;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
@Value.Style(builder = "new")
@JsonSerialize(as = ImmutableTimetableConfigDTO.class)
@JsonDeserialize(as = ImmutableTimetableConfigDTO.class, builder = ImmutableTimetableConfigDTO.Builder.class)
public interface TimetableConfigDTO {

    static ImmutableTimetableConfigDTO.Builder builder() {
        return new ImmutableTimetableConfigDTO.Builder();
    }

    List<LessonConfigDTO> getConfig();

    int getLessonCount();
}
