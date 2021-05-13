package com.sms.homeworkservice.file.control;

import com.sms.api.homework.FileLinkDTO;
import com.sms.model.homework.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

public class FileMapper {

    private FileMapper() {
    }

    public static FileLinkDTO toDTO(FileInfoJPA jpa) {
        return FileLinkDTO.builder()
                .id(jpa.getId())
                .filename(jpa.getFilename())
                .size(jpa.getSize())
                .uri(getUri(jpa))
                .build();
    }

    private static String getUri(FileInfoJPA jpa) {
        return ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/files")
                .path("/id/")
                .path(jpa.getId().toString())
                .path("/type/")
                .path(jpa.getType())
                .toUriString();
    }
}
