package com.sms.homeworkservice.file.boundary;

import com.sms.api.usermanagement.UserDTO;
import com.sms.context.AuthRole;
import com.sms.homeworkservice.file.control.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/files")
@Scope("request")
public class FileResource {

    @Autowired
    FileService fileService;

    @DeleteMapping("/{id}")
    @AuthRole({UserDTO.Role.TEACHER, UserDTO.Role.STUDENT})
    public ResponseEntity<Object> deleteFile(@PathVariable("id") Long id) {
        fileService.deleteFile(id);
        return ResponseEntity.noContent().build();
    }
}