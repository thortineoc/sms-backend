package com.sms.model.homework;

import javax.persistence.*;

@Entity
@Table(name = "answersfiles")
public class AnswerFileDetailJPA extends FileJPA implements FileDetailJPA {

    @ManyToOne(fetch = FetchType.LAZY)
    private AnswerJPA answer;

    private byte[] file;

    public byte[] getFile() {
        return file;
    }
}
