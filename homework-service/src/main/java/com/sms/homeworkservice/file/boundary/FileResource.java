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
import org.springframework.http.HttpStatus;
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
        return ResponseEntity.status(HttpStatus.OK).body(fileService.store(file, id, type));
    }

    @AuthRole({UserDTO.Role.STUDENT, UserDTO.Role.TEACHER})
    @GetMapping(value = "/id/{id}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<ByteArrayResource> getFile(@PathVariable Long id) {
        FileJPA dbFile = fileService.getFile(id);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + dbFile.getFilename() + "\"")
                .body(new ByteArrayResource(dbFile.getFile()));
    }

}
