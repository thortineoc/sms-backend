package com.sms.api.timetables;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.util.List;
import java.util.Optional;

@Value.Immutable
@Value.Style(builder = "new")
@JsonSerialize(as = ImmutableLessonDTO.class)
@JsonDeserialize(as = ImmutableLessonDTO.class, builder = ImmutableLessonDTO.Builder.class)
public interface LessonDTO {

    static ImmutableLessonDTO.Builder builder() {
        return new ImmutableLessonDTO.Builder();
    }

    Integer getLesson();

    String getGroup();

    String getSubject();

    Optional<String> getTeacherId();

    Integer getWeekDay();

    Optional<String> getRoom();

    List<LessonDTO> getConflicts();
}
