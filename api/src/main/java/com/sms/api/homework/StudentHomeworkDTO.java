package com.sms.api.homework;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.util.List;
import java.util.Optional;

@Value.Immutable
@Value.Style(builder = "new")
@JsonSerialize(as = ImmutableStudentHomeworkDTO.class)
@JsonDeserialize(as = ImmutableStudentHomeworkDTO.class, builder = ImmutableStudentHomeworkDTO.Builder.class)
public interface StudentHomeworkDTO extends SimpleHomeworkDTO {

    static ImmutableStudentHomeworkDTO.Builder builder() {
        return new ImmutableStudentHomeworkDTO.Builder();
    }

    Optional<AnswerDTO> getAnswer();

    List<FileLinkDTO> getFiles();

}
