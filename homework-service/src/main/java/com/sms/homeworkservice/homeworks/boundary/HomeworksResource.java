package com.sms.homeworkservice.homeworks.boundary;


import com.sms.context.AuthRole;
import com.sms.homeworks.HomeworkDTO;
import com.sms.homeworks.HomeworkFileDTO;
import com.sms.homeworkservice.homeworks.control.HomeworksService;
import com.sms.homeworkservice.homeworks.control.repository.HomeworkFilesJPA;
import com.sms.usermanagement.UserDTO;
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
import java.util.List;

@RestController
@RequestMapping("/homeworks")
@Scope("request")
public class HomeworksResource {

    @Autowired
    HomeworksService homeworksService;

    @PutMapping
    @AuthRole(UserDTO.Role.TEACHER)
    public ResponseEntity<HomeworkDTO> updateHomework(@RequestBody HomeworkDTO homeworkDTO) {
        HomeworkDTO homework = homeworksService.updateHomework(homeworkDTO);
        return ResponseEntity.ok(homework);
    }

    @PostMapping("/upload/{id}")
    @AuthRole({UserDTO.Role.STUDENT, UserDTO.Role.TEACHER})
    public ResponseEntity<HomeworkFileDTO> uploadFile(@PathVariable("id") Integer homework, @RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.status(HttpStatus.OK).body(homeworksService.store(file, homework));
    }

    @GetMapping("/files/{id}")
    public ResponseEntity<List<HomeworkFileDTO>> getListFiles(@PathVariable Integer id) {
        List<HomeworkFileDTO> filesInfo = homeworksService.getFilesInfo(id);
        return ResponseEntity.status(HttpStatus.OK).body(filesInfo);
    }

    @GetMapping(value = "/file/{id}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<ByteArrayResource> getFile(@PathVariable Long id) {
        HomeworkFilesJPA dbFile = homeworksService.getFile(id);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + dbFile.getFileName() + "\"")
                .body(new ByteArrayResource(dbFile.getFile()));
    }

}
