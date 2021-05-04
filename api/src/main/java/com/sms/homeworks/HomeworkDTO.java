package com.sms.homeworks;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.sms.grades.ImmutableGradeDTO;
import org.immutables.value.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Value.Immutable
@Value.Style(builder = "new" )
@JsonSerialize(as = ImmutableHomeworkDTO.class)
@JsonDeserialize(as = ImmutableHomeworkDTO.class, builder = ImmutableHomeworkDTO.Builder.class)
public interface HomeworkDTO {

    static ImmutableHomeworkDTO.Builder builder() { return new ImmutableHomeworkDTO.Builder(); }

    Optional<Long> getId();

    String getTitle();

    String getDescription();

    LocalDateTime getDeadline();

    String getSubject();

    Optional<String> getTeacherId();

    Optional<Byte[]> getFile();




    }



