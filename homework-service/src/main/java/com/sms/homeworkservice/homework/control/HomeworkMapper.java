package com.sms.homeworkservice.homework.control;

import com.sms.api.homework.AnswerDTO;
import com.sms.api.homework.HomeworkDTO;
import com.sms.api.homework.ImmutableHomeworkDTO;
import com.sms.api.homework.SimpleHomeworkDTO;
import com.sms.homeworkservice.file.control.FileMapper;
import com.sms.model.homework.HomeworkJPA;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class HomeworkMapper {

    public static HomeworkJPA toJPA(HomeworkDTO homework) {
        HomeworkJPA jpa = new HomeworkJPA();
        jpa.setDeadline(Timestamp.valueOf(homework.getDeadline()));
        jpa.setSubject(homework.getSubject());
        jpa.setGroup(homework.getGroup());
        jpa.setTitle(homework.getTitle());
        jpa.setToEvaluate(homework.getToEvaluate());
        homework.getDescription().ifPresent(jpa::setDescription);
        homework.getId().ifPresent(jpa::setId);
        homework.getCreatedTime().map(Timestamp::valueOf).ifPresent(jpa::setCreatedTime);
        homework.getLastUpdateTime().map(Timestamp::valueOf).ifPresent(jpa::setLastUpdatedTime);
        homework.getTeacherId().ifPresent(jpa::setTeacherId);
        return updatedTime(jpa);
    }

    public static HomeworkDTO toDetailDTO(HomeworkJPA jpa, List<AnswerDTO> answers) {
        return toDTOBuilder(jpa)
                .answers(answers)
                .files(jpa.getFiles().stream()
                        .map(FileMapper::toDTO)
                        .collect(Collectors.toList()))
                .build();
    }

    public static Map<String, Map<String, List<SimpleHomeworkDTO>>> toTreeDTO(List<HomeworkJPA> jpa) {
        Map<String, List<SimpleHomeworkDTO>> bySubject = toDTOsBySubject(jpa);

        return bySubject.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().stream()
                        .collect(Collectors.groupingBy(SimpleHomeworkDTO::getGroup))));
    }

    public static Map<String, List<SimpleHomeworkDTO>> toDTOsBySubject(List<HomeworkJPA> jpa) {
        return jpa.stream()
                .map(HomeworkMapper::toDTO)
                .collect(Collectors.groupingBy(SimpleHomeworkDTO::getSubject));
    }

    public static SimpleHomeworkDTO toDTO(HomeworkJPA jpa) {
        return toDTOBuilder(jpa).build();
    }


    public static ImmutableHomeworkDTO.Builder toDTOBuilder(HomeworkJPA jpa) {
        return HomeworkDTO.builder()
                .id(Optional.ofNullable(jpa.getId()))
                .createdTime(Optional.ofNullable(jpa.getCreatedTime()).map(Timestamp::toLocalDateTime))
                .lastUpdateTime(Optional.ofNullable(jpa.getLastUpdatedTime()).map(Timestamp::toLocalDateTime))
                .description(Optional.ofNullable(jpa.getDescription()))
                .title(jpa.getTitle())
                .deadline(jpa.getDeadline().toLocalDateTime())
                .group(jpa.getGroup())
                .subject(jpa.getSubject())
                .teacherId(Optional.ofNullable(jpa.getTeacherId()))
                .toEvaluate(jpa.getToEvaluate());
    }

    private static HomeworkJPA updatedTime(HomeworkJPA homeworkJPA) {
        if (homeworkJPA.getId() == null) {
            homeworkJPA.setCreatedTime(Timestamp.valueOf(LocalDateTime.now()));
        }
        homeworkJPA.setLastUpdatedTime(Timestamp.valueOf(LocalDateTime.now()));
        return homeworkJPA;
    }


}
