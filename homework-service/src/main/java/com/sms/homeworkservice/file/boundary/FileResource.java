package com.sms.homeworkservice.file.boundary;

import com.sms.api.homework.FileLinkDTO;
import com.sms.api.usermanagement.UserDTO;
import com.sms.context.AuthRole;
import com.sms.homeworkservice.file.control.FileService;
import com.sms.model.homework.FileDetailJPA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.ws.rs.QueryParam;
import java.io.IOException;

@RestController
@RequestMapping("/files")
@Scope("request")
public class FileResource {

    @Autowired
    FileService fileService;

    @PutMapping("/upload/")
    @AuthRole({UserDTO.Role.STUDENT, UserDTO.Role.TEACHER})
    public ResponseEntity<FileLinkDTO> uploadFile(@QueryParam("id") Long id, @QueryParam("type") FileLinkDTO.Type type, @RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.status(HttpStatus.OK).body(fileService.store(file, id, type));
    }

    @AuthRole({UserDTO.Role.STUDENT, UserDTO.Role.TEACHER})
    @GetMapping(value = "/id/{id}/type/{type}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<ByteArrayResource> getFile(@PathVariable Long id, @PathVariable FileLinkDTO.Type type) {
        FileDetailJPA dbFile = fileService.getFile(id, type);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + dbFile.getFilename() + "\"")
                .body(new ByteArrayResource(dbFile.getFile()));
    }

}
