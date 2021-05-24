package com.sms.api.timetable;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.http.HttpMessage;
import org.immutables.value.Value;

@Value.Immutable
@Value.Style(builder = "new")
@JsonSerialize(as = ImmutableTimetableConflictDTO.class)
@JsonDeserialize(as = ImmutableTimetableConflictDTO.class, builder = ImmutableTimetableConflictDTO.Builder.class)
public interface TimetableConflictDTO extends TimetableDTO{

    static ImmutableTimetableConflictDTO.Builder builder(){ return  new ImmutableTimetableConflictDTO.Builder(); }
    HttpMessage getMessage();

}
