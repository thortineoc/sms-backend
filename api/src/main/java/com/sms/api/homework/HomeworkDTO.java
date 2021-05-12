package com.sms.api.homework;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Value.Immutable
@Value.Style(builder = "new")
@JsonSerialize(as = ImmutableHomeworkDTO.class)
@JsonDeserialize(as = ImmutableHomeworkDTO.class, builder = ImmutableHomeworkDTO.Builder.class)
public interface HomeworkDTO extends SimpleHomeworkDTO {

    static ImmutableHomeworkDTO.Builder builder() {
        return new ImmutableHomeworkDTO.Builder();
    }

    List<AnswerDTO> getAnswers();

    List<FileLinkDTO> getFiles();

}