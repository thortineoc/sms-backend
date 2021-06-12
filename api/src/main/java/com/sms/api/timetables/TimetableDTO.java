package com.sms.api.timetables;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.sms.api.usermanagement.UserDTO;
import org.immutables.value.Value;

import java.util.List;
import java.util.Map;

@Value.Immutable
@Value.Style(builder = "new")
@JsonSerialize(as = ImmutableTimetableDTO.class)
@JsonDeserialize(as = ImmutableTimetableDTO.class, builder = ImmutableTimetableDTO.Builder.class)
public interface TimetableDTO {

    static ImmutableTimetableDTO.Builder builder() {
        return new ImmutableTimetableDTO.Builder();
    }

    List<LessonDTO> getLessons();

    Map<String, UserDTO> getTeachers();

}
