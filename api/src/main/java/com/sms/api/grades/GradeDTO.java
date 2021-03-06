package com.sms.api.grades;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Value.Immutable
@Value.Style(builder = "new")
@JsonSerialize(as = ImmutableGradeDTO.class)
@JsonDeserialize(as = ImmutableGradeDTO.class, builder = ImmutableGradeDTO.Builder.class)
public interface GradeDTO {

    static ImmutableGradeDTO.Builder builder() {
        return new ImmutableGradeDTO.Builder();
    }

    Optional<Long> getId();

    String getSubject();

    Optional<String> getTeacherId();

    Optional<LocalDateTime> getModifyTime();

    Optional<LocalDateTime> getCreatedTime();

    String getStudentId();

    BigDecimal getGrade();

    @Value.Default
    default Boolean isFinal() {
        return false;
    }

    Optional<String> getDescription();

    @Value.Default
    default Integer getWeight() {
        return 1;
    }
}
