package com.sms.model.homework;

import org.springframework.web.multipart.MultipartFile;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.IOException;

@Entity
@Table(name = "answersfiles")
public class AnswerFileDetailJPA extends FileJPA implements FileDetailJPA {

    @ManyToOne(fetch = FetchType.LAZY)
    private AnswerJPA answer;

    private byte[] file;

    public byte[] getFile() {
        return file;
    }

    public void setFile(MultipartFile file) throws IOException { this.file= file.getBytes();}
}
