package com.sms.api.homework;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.sms.api.grades.GradeDTO;
import com.sms.api.usermanagement.UserDTO;
import org.immutables.value.Value;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Value.Immutable
@Value.Style(builder = "new")
@JsonSerialize(as = ImmutableAnswerDTO.class)
@JsonDeserialize(as = ImmutableAnswerDTO.class, builder = ImmutableAnswerDTO.Builder.class)
public interface AnswerDTO {

    static ImmutableAnswerDTO.Builder builder() {
        return new ImmutableAnswerDTO.Builder();
    }

    Optional<Long> getId();

    Optional<UserDTO> getStudent();

    Optional<String> getReview();

    List<FileLinkDTO> getFiles();

    Optional<LocalDateTime> getCreatedTime();

    Optional<LocalDateTime> getLastUpdatedTime();

    Optional<GradeDTO> getGrade();
}
