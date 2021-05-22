package com.sms.homeworkservice.file.boundary;

import com.sms.api.homework.FileLinkDTO;
import com.sms.api.usermanagement.UserDTO;
import com.sms.context.AuthRole;
import com.sms.homeworkservice.file.control.FileService;
import com.sms.model.homework.FileJPA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/files")
@Scope("request")
public class FileResource {

    @Autowired
    FileService fileService;

    @PostMapping("/upload/{id}/{type}")
    @AuthRole({UserDTO.Role.STUDENT, UserDTO.Role.TEACHER})
    public ResponseEntity<FileLinkDTO> uploadFile(@PathVariable("id") Long id, @PathVariable("type") FileLinkDTO.Type type, @RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(fileService.store(file, id, type));
    }

    @AuthRole({UserDTO.Role.STUDENT, UserDTO.Role.TEACHER, UserDTO.Role.ADMIN})
    @GetMapping(value = "/id/{id}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<ByteArrayResource> getFile(@PathVariable Long id) {
        return fileService.getFile(id).map(this::buildFileResponse).orElse(ResponseEntity.noContent().build());
    }

    @DeleteMapping("/{id}")
    @AuthRole({UserDTO.Role.TEACHER, UserDTO.Role.STUDENT, UserDTO.Role.ADMIN})
    public ResponseEntity<Object> deleteFile(@PathVariable("id") Long id) {
        fileService.deleteFile(id);
        return ResponseEntity.noContent().build();
    }

    private ResponseEntity<ByteArrayResource> buildFileResponse(FileJPA file) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                .body(new ByteArrayResource(file.getFile()));
    }
}
