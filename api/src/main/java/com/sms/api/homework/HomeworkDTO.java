package com.sms.api.homework;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
@Value.Style(builder = "new")
@JsonSerialize(as = ImmutableHomeworkDTO.class)
@JsonDeserialize(as = ImmutableHomeworkDTO.class, builder = ImmutableHomeworkDTO.Builder.class)
public interface HomeworkDTO extends SimpleHomeworkDTO {

    static ImmutableHomeworkDTO.Builder builder() {
        return new ImmutableHomeworkDTO.Builder();
    }

    List<AnswerWithStudentDTO> getAnswers();

    List<FileLinkDTO> getFiles();

}