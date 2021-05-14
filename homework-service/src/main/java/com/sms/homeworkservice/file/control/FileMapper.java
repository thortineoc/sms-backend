package com.sms.homeworkservice.file.control;

import com.sms.api.homework.FileLinkDTO;
import com.sms.model.homework.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;

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

    public static FileDetailJPA toJPA(MultipartFile file, String filename, Long id) throws IOException {
        FileDetailJPA jpa= new FileDetailJPA();
        jpa.setFilename(filename);
        jpa.setSize(file.getSize());
        jpa.setFile(file.getBytes());
        jpa.setRelationId(id);
        return jpa;
    }

}
