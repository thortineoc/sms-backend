package com.sms.homeworkservice.file.control;

import com.sms.api.homework.FileLinkDTO;
import com.sms.model.homework.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;

public class FileMapper {

    private static final String TYPE_QP = "type";

    private FileMapper() {
    }

    public static FileLinkDTO toDTO(FileJPA jpa) {
        return FileLinkDTO.builder()
                .id(jpa.getId())
                .filename(jpa.getFilename())
                .size(jpa.getSize())
                .uri(getUri(jpa))
                .build();
    }

    public static HomeworkFileDetailJPA toJPA(MultipartFile file, String filename, Long id) throws IOException {
        HomeworkFileDetailJPA jpa= new HomeworkFileDetailJPA();
        jpa.setFilename(filename);
        jpa.setSize(file.getSize());
        jpa.setFile(file);
        jpa.setHomeworkID(id);
        jpa.setId(id*id);
        return jpa;
    }


    private static String getUri(FileJPA jpa) {
        return ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/files/")
                .path(jpa.getId().toString())
                .queryParam(TYPE_QP, getType(jpa))
                .toUriString();
    }

    private static FileLinkDTO.Type getType(FileJPA jpa) {
        if (jpa instanceof HomeworkFileJPA || jpa instanceof HomeworkFileDetailJPA) {
            return FileLinkDTO.Type.HOMEWORK;
        } else if (jpa instanceof AnswerFileJPA || jpa instanceof AnswerFileDetailJPA) {
            return FileLinkDTO.Type.ANSWER;
        } else {
            throw new IllegalStateException("You shouldn't be here: wrong type of FileJPA object: " + jpa.getClass().toString());
        }
    }


}
