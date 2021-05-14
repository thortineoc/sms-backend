package com.sms.homeworkservice.file.control;

import com.sms.api.homework.FileLinkDTO;
import com.sms.model.homework.*;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.util.Objects;

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

    public static FileLinkDTO toDTO(FileDetailJPA jpa){
        return FileLinkDTO.builder()
                .id(jpa.getId())
                .filename(jpa.getFilename())
                .size(jpa.getSize())
                .uri(getUri(jpa))
                .build();
    }

    private static String getUri(FileDetailJPA jpa) {
        return ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/files")
                .path("/id/")
                .path(jpa.getId().toString())
                .path("/type/")
                .path(jpa.getType())
                .toUriString();
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

    public static FileDetailJPA toJPA(MultipartFile file, Long id, FileLinkDTO.Type type) throws IOException {
        FileDetailJPA jpa= new FileDetailJPA();
        jpa.setFilename(StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename())));
        jpa.setSize(file.getSize());
        jpa.setFile(file.getBytes());
        jpa.setRelationId(id);
        jpa.setType(type.name());
        return jpa;
    }

}
