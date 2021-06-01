package com.sms.api.timetables;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.sms.api.usermanagement.UserDTO;
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
@Value.Style(builder = "new")
@JsonSerialize(as = ImmutableTeacherInfoDTO.class)
@JsonDeserialize(as = ImmutableTeacherInfoDTO.class, builder = ImmutableTeacherInfoDTO.Builder.class)
public interface TeacherInfoDTO {

    static ImmutableTeacherInfoDTO.Builder builder() {
        return new ImmutableTeacherInfoDTO.Builder();
    }

    UserDTO getTeacher();

    Integer getLessonCount();

    List<LessonDTO> getConflicts();

}
