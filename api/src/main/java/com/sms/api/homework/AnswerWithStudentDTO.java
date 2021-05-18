package com.sms.api.homework;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.sms.api.usermanagement.UserDTO;
import org.immutables.value.Value;

import java.util.Optional;

@Value.Immutable
@Value.Style(builder = "new")
@JsonSerialize(as = ImmutableAnswerWithStudentDTO.class)
@JsonDeserialize(as = ImmutableAnswerWithStudentDTO.class, builder = ImmutableAnswerWithStudentDTO.Builder.class)
public interface AnswerWithStudentDTO {

    static ImmutableAnswerWithStudentDTO.Builder builder() {
        return new ImmutableAnswerWithStudentDTO.Builder();
    }

    UserDTO getStudent();

    Optional<AnswerDTO> getAnswer();
}
