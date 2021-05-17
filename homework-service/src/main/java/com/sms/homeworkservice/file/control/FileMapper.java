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

    public static FileLinkDTO toDTO(FileJPA jpa){
        return FileLinkDTO.builder()
                .id(jpa.getId())
                .filename(jpa.getFilename())
                .size(jpa.getSize())
                .uri(getUri(jpa))
                .build();
    }


    private static String getUri(FileJPA jpa) {
        return ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/files")
                .path("/id/")
                .path(jpa.getId().toString())
                .toUriString();
    }

    private static String getUri(FileInfoJPA jpa) {
        return ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/files")
                .path("/id/")
                .path(jpa.getId().toString())
                .toUriString();
    }

    public static FileJPA toJPA(MultipartFile file, Long id, FileLinkDTO.Type type, String ownerId) throws IOException {
        FileJPA jpa= new FileJPA();
        jpa.setFilename(StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename())));
        jpa.setSize(file.getSize());
        jpa.setFile(file.getBytes());
        jpa.setRelationId(id);
        jpa.setType(type);
        jpa.setOwnerId(ownerId);
        return jpa;
    }

}
