package com.sms.model.homework;

import org.springframework.web.multipart.MultipartFile;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.IOException;

@Entity
@Table(name = "homeworksfiles")
public class HomeworkFileDetailJPA extends FileJPA implements FileDetailJPA {

    @ManyToOne(fetch = FetchType.LAZY)
    private HomeworkJPA homework;

    private byte[] file;
    private Long homeworkID;

    public byte[] getFile() {
        return file;
    }
    public Long getHomeworkID() {return homeworkID;}

    @Override
    public void setFile(MultipartFile file) throws IOException {
    this.file=file.getBytes();
    }
    public void setHomeworkID(Long id) {this.homeworkID=id;}

}
