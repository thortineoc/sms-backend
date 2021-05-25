package com.sms.api.timetable;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;
import org.springframework.http.HttpStatus;

import java.util.List;

@Value.Immutable
@Value.Style(builder = "new")
@JsonSerialize(as = ImmutableTimetableConflictDTO.class)
@JsonDeserialize(as = ImmutableTimetableConflictDTO.class, builder = ImmutableTimetableConflictDTO.Builder.class)
public interface TimetableConflictDTO extends SimpleTimetableDTO {

    static ImmutableTimetableConflictDTO.Builder builder() {
        return new ImmutableTimetableConflictDTO.Builder();
    }

    List<String> getInfo();

}
