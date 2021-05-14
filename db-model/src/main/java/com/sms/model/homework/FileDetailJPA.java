package com.sms.model.homework;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "files")
public class FileDetailJPA extends FileBaseJPA {

    private byte[] file;

    public byte[] getFile() {
        return file;
    }
    public void setFile(byte[] file) {
        this.file = file;
    }
}
