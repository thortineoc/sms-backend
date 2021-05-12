package com.sms.api.homework;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.time.LocalDateTime;
import java.util.Optional;

@Value.Immutable
@Value.Style(builder = "new")
@JsonSerialize(as = ImmutableSimpleHomeworkDTO.class)
@JsonDeserialize(as = ImmutableSimpleHomeworkDTO.class, builder = ImmutableSimpleHomeworkDTO.Builder.class)
public interface SimpleHomeworkDTO {

    static ImmutableSimpleHomeworkDTO.Builder builder() {
        return new ImmutableSimpleHomeworkDTO.Builder();
    }

    Optional<Long> getId();

    String getTitle();

    String getGroup();

    String getSubject();

    LocalDateTime getDeadline();

    Optional<String> getTeacherId();

    Optional<String> getDescription();

    Optional<LocalDateTime> getCreatedTime();

    Optional<LocalDateTime> getLastUpdateTime();

    @Value.Default
    default Boolean getToEvaluate() {
        return Boolean.TRUE;
    }
}
