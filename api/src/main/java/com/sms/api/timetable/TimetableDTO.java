package com.sms.api.timetable;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
@Value.Style(builder = "new")
@JsonSerialize(as = ImmutableTimetableDTO.class)
@JsonDeserialize(as = ImmutableTimetableDTO.class, builder = ImmutableTimetableDTO.Builder.class)
public interface TimetableDTO extends SimpleTimetableDTO{

    static ImmutableTimetableDTO.Builder builder(){ return  new ImmutableTimetableDTO.Builder(); }
    List<List<SimpleTimetableDTO>> getUserTimetable();

}
