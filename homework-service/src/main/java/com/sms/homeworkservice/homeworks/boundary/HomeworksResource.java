package com.sms.homeworkservice.homeworks.boundary;


import com.sms.context.AuthRole;
import com.sms.homeworks.HomeworkDTO;
import com.sms.homeworkservice.homeworks.control.HomeworksService;
import com.sms.homeworkservice.homeworks.control.repository.FileJPA;
import com.sms.homeworkservice.homeworks.control.response.ResponseFile;
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
    public ResponseEntity<ResponseFile> uploadFile(@PathVariable("id") Integer homework, @RequestParam("file") MultipartFile file) throws IOException {
        ResponseFile fileJPA = homeworksService.store(file, homework);
        return ResponseEntity.ok(fileJPA);
    }


    //tu wyświetla jsona z np linkami do pobrania typu: http://localhost:24026/homework-service/files/monochord.pdf
    //do zadania o id {id}
    //niestety wywala 404 więc nwm czy nie ma prawa to działać, czy my mamy coś zablokowane
    //czy lokalnie to nie będzie działać
    @GetMapping("/files/{id}")
    public ResponseEntity<List<ResponseFile>> getListFiles(@PathVariable Integer id) {
        List<ResponseFile> fileInfos = homeworksService.getFilesInfo(id);
        return ResponseEntity.status(HttpStatus.OK).body(fileInfos);
    }

    //to do wyświetlaniafil plików
    //nie wiem czy będziemy tego używać
    //zamiast file type to MultipartFile file.getContentType() -> to do bazy (nie mamy takiego pola)
    //na insomii pokazuje hinduskie znaczki, bo binarka ale jak się da Preview/Save Raw Resposne
    //to normalnie zapisuje docx/pdf/czy co tam chcemy
    @GetMapping("/file/{id}")
    public ResponseEntity<ByteArrayResource> getFile(@PathVariable Long id) {
        String filetype = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        FileJPA dbFile = homeworksService.getFile(id);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(filetype))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + dbFile.getFileName() + "\"")
                .body(new ByteArrayResource(dbFile.getFile()));
    }

}
