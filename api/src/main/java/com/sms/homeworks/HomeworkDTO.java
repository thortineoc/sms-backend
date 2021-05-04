package com.sms.homeworks;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

@Value.Immutable
@Value.Style(builder = "new" )
@JsonSerialize(as = ImmutableHomeworkDTO.class)
@JsonDeserialize(as = ImmutableHomeworkDTO.class, builder = ImmutableHomeworkDTO.Builder.class)
public interface HomeworkDTO {

    static ImmutableHomeworkDTO.Builder builder() {
        return new ImmutableHomeworkDTO.Builder();
    }

    Optional<Long> getId();

    String getTitle();

    String getDescription();

    String getGroup();

    String getSubject();

    String getDeadline();

    Optional<Byte[]> getFile();

    Optional<String> getTeacherId();

    @Value.Default
    default Boolean getToEvaluate() {
        return true;
    }


}



