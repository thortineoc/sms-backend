package com.sms.api.timetables;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
@Value.Style(builder = "new")
@JsonSerialize(as = ImmutableLessonsDTO.class)
@JsonDeserialize(as = ImmutableLessonsDTO.class, builder = ImmutableLessonsDTO.Builder.class)
public interface LessonsDTO {

    static ImmutableLessonsDTO.Builder builder() {
        return new ImmutableLessonsDTO.Builder();
    }

    List<List<LessonDTO>> getLessons();
}
