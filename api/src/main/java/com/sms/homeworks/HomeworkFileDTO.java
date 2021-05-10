package com.sms.homeworks;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@Value.Style(builder = "new" )
@JsonSerialize(as = ImmutableHomeworkFileDTO.class)
@JsonDeserialize(as = ImmutableHomeworkFileDTO.class, builder = ImmutableHomeworkFileDTO.Builder.class)
public interface HomeworkFileDTO {

    static ImmutableHomeworkFileDTO.Builder builder() {
        return new ImmutableHomeworkFileDTO.Builder();
    }

    String getName();
    String getUrl();
    Integer getSize();
    Long getFileID();


}
