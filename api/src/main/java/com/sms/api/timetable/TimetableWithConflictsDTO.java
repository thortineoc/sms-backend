package com.sms.api.timetable;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.checkerframework.checker.units.qual.C;
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
@Value.Style(builder = "new")
@JsonSerialize(as = ImmutableTimetableWithConflictsDTO.class)
@JsonDeserialize(as = ImmutableTimetableWithConflictsDTO.class, builder = ImmutableTimetableWithConflictsDTO.Builder.class)
public interface TimetableWithConflictsDTO{

    static ImmutableTimetableWithConflictsDTO.Builder builder(){ return  new ImmutableTimetableWithConflictsDTO.Builder(); }
    List<List<ConflictDTO>> getTimetableWithConflicts();
}
