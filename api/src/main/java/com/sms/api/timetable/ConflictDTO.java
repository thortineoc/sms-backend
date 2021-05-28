package com.sms.api.timetable;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.util.Optional;

@Value.Immutable
@Value.Style(builder = "new")
@JsonSerialize(as = ImmutableConflictDTO.class)
@JsonDeserialize(as = ImmutableConflictDTO.class, builder = ImmutableConflictDTO.Builder.class)
public interface ConflictDTO extends  SimpleTimetableDTO{

    static ImmutableConflictDTO.Builder builder(){return  new ImmutableConflictDTO.Builder(); }
    Optional<SimpleTimetableDTO> getConflict();
}
