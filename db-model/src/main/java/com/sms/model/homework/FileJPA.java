package com.sms.model.homework;

import javax.persistence.*;

@MappedSuperclass
public class FileJPA {

    @Id
    @GeneratedValue( strategy = GenerationType.SEQUENCE)
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

    public void setId(Long id) {
         this.id=id;
    }

    public void setSize(Long size) {
         this.size=size;
    }

    public void setFilename(String filename) {
        this.filename=filename;
    }

    public FileJPA(Long id, String filename, Long size){
        this.id=id;
        this.filename=filename;
        this.size=size;
    }
    FileJPA(){};
}
