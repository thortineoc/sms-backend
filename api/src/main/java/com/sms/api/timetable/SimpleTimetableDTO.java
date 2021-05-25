package com.sms.api.timetable;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.time.LocalDateTime;
import java.util.Optional;

@Value.Immutable
@Value.Style(builder = "new")
@JsonSerialize(as = ImmutableSimpleTimetableDTO.class)
@JsonDeserialize(as = ImmutableSimpleTimetableDTO.class, builder = ImmutableSimpleTimetableDTO.Builder.class)
public interface SimpleTimetableDTO {

    static ImmutableSimpleTimetableDTO.Builder builder(){return  new ImmutableSimpleTimetableDTO.Builder(); }

    Optional<Long> getId();

    String getTeacherId();

    String getGroup();

    String getSubject();

    Integer getWeekday();

    Integer getRoom();

    LocalDateTime getBegindate();

    LocalDateTime getEnddate();

    Integer getLesson();


}
