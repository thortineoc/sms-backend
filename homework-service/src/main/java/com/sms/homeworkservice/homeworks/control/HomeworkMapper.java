package com.sms.homeworkservice.homeworks.control;

import com.sms.homeworks.HomeworkDTO;
import com.sms.homeworks.HomeworkFileDTO;
import com.sms.homeworkservice.homeworks.control.repository.HomeworkFilesJPA;
import com.sms.homeworkservice.homeworks.control.repository.HomeworkJPA;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.sql.Timestamp;
import java.util.Optional;

public class HomeworkMapper {

    public HomeworkMapper() {

    }

    public static HomeworkDTO toDTO(HomeworkJPA homeworkJPA) {
        return HomeworkDTO.builder()
                .id(Optional.ofNullable(homeworkJPA.getId()))
                .description(Optional.ofNullable(homeworkJPA.getDescription()))
                .title(homeworkJPA.getTitle())
                .subject(homeworkJPA.getSubject())
                .teacherId(homeworkJPA.getTeacherid())
                .deadline(timestampToDeadline(homeworkJPA.getDeadline()))
                .toEvaluate(homeworkJPA.getToevaluate())
                .file(Optional.ofNullable(homeworkJPA.getFile()))
                .group(homeworkJPA.getGroups())
                .build();
    }

    public static HomeworkJPA toJPA(HomeworkDTO homeworkDTO) {
        HomeworkJPA homeworkJPA = new HomeworkJPA();
        homeworkDTO.getId().ifPresent(homeworkJPA::setId);
        homeworkDTO.getDescription().ifPresent(homeworkJPA::setDescription);
        homeworkJPA.setTitle(homeworkDTO.getTitle());
        homeworkJPA.setSubject(homeworkDTO.getSubject());
        homeworkDTO.getTeacherId().ifPresent(homeworkJPA::setTeacherid);
        homeworkJPA.setDeadline(deadlineToTimestamp(homeworkDTO.getDeadline()));
        homeworkJPA.setToevaluate(homeworkDTO.getToEvaluate());
        homeworkDTO.getFile().ifPresent(homeworkJPA::setFile);
        homeworkJPA.setGroups(homeworkDTO.getGroup());
        return homeworkJPA;
    }

    public static HomeworkFileDTO toFileDTO(HomeworkFilesJPA homeworkDTO){
        return HomeworkFileDTO.builder()
                .fileID(homeworkDTO.getId())
                .name(homeworkDTO.getFileName())
                .size(homeworkDTO.getSize())
                .url(createDownloadUri(homeworkDTO))
                .build();
    }

    private static Timestamp deadlineToTimestamp(String date){
        return Timestamp.valueOf(date);
    }

    private static String timestampToDeadline(Timestamp date){
        return date.toString();
    }

    private static String createDownloadUri(HomeworkFilesJPA file) {
        return ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/homeworks/file/")
                .path(file.getId().toString())
                .toUriString();
    }

}
