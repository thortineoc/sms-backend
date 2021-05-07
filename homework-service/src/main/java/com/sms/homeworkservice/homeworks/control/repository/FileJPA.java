package com.sms.homeworkservice.homeworks.control.repository;

import javax.persistence.*;


@Entity
@Table(name = "homeworksfiles")
public class FileJPA {

    @Id
    @Column(name = "id")
    @GeneratedValue(generator = "homeworksfiles_id_seq")
    private Long id;

    @Column(name = "homework_id")
    private int homeworkid;

    @Column(name = "filename")
    private String fileName;

    @Column(name = "size")
    private int size;

    @Column(name = "file")
    private byte[] file;

    //setters

    public void setId(Long id) {
        this.id = id;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    public void setHomeworkid(int homework_id){this.homeworkid=homework_id;};
    public void setFile(byte[] file){this.file=file;}
    public void setSize(int size){this.size=size;}

    //getters

    public Long getId() {
        return id;
    }

    public Integer getHomeworkid() {
        return homeworkid;
    }

    public String getFileName() {
        return fileName;
    }

    public byte[] getFile() {
        return file;
    }

    public Integer getSize() {
        return size;
    }

    public FileJPA() {

    }

    public FileJPA(Integer homeworkid, String filename, Integer size, byte[] data) {
        this.file = data;
        this.homeworkid = homeworkid;
        this.fileName = filename;
        this.size = size;
    }


}
