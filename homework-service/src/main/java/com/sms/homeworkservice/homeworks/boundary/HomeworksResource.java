package com.sms.homeworkservice.homeworks.boundary;


import com.sms.context.AuthRole;
import com.sms.homeworks.HomeworkDTO;
import com.sms.homeworkservice.homeworks.control.HomeworksService;
import com.sms.homeworkservice.homeworks.control.repository.FileJPA;
import com.sms.homeworkservice.homeworks.control.response.ResponseMessage;
import com.sms.usermanagement.UserDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RestController
@RequestMapping("/homeworks")
@Scope("request")
public class HomeworksResource {

    @Autowired
    HomeworksService homeworksService;

    @PutMapping
    @AuthRole(UserDTO.Role.TEACHER)
    public ResponseEntity<HomeworkDTO> updateHomework(@RequestBody HomeworkDTO homeworkDTO){
         homeworksService.updateHomework(homeworkDTO);
        return ResponseEntity.ok(homeworkDTO);
    }

    @PostMapping("/upload")
    @AuthRole({UserDTO.Role.STUDENT, UserDTO.Role.TEACHER})
    public ResponseEntity<ResponseMessage> uploadFile(@RequestParam("file") MultipartFile file)
    {
        String message="";
        try {
            homeworksService.store(file);
            message = "Uploaded the file successfully: " + file.getOriginalFilename();
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
        } catch (Exception e) {
            message = "Could not upload the file: " + file.getOriginalFilename() + "!";
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
        }
    }

    @GetMapping("/files/{id}")
    public ResponseEntity<byte[]> getFile(@PathVariable Long id) {
        FileJPA fileDB = homeworksService.getFile(id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileDB.getFileName() + "\"")
                .body(fileDB.getFile());
    }



}
