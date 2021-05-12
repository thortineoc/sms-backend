package com.sms.api.grades;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.util.List;
import java.util.Optional;

@Value.Immutable
@Value.Style(builder = "new")
@JsonSerialize(as = ImmutableGradesDTO.class)
@JsonDeserialize(as = ImmutableGradesDTO.class, builder = ImmutableGradesDTO.Builder.class)
public interface GradesDTO {

    static ImmutableGradesDTO.Builder builder() {
        return new ImmutableGradesDTO.Builder();
    }

    Optional<GradeDTO> getFinalGrade();

    List<GradeDTO> getGrades();
}
