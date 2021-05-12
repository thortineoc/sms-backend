package com.sms.model.homework;

import javax.persistence.*;

@MappedSuperclass
public class FileJPA {

    @Id
    private Long id;

    private String filename;

    private Long size;

    public Long getId() {
        return id;
    }

    public Long getSize() {
        return size;
    }

    public String getFilename() {
        return filename;
    }
}
