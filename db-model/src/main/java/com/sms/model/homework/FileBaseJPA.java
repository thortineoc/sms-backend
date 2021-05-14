package com.sms.model.homework;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class FileBaseJPA {

    @Id
    @GeneratedValue(generator = "files_id_seq")
    private Long id;

    private String filename;

    private Long size;

    public void setId(Long id) {
        this.id = id;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setRelationId(Long relationId) {
        this.relationId = relationId;
    }

    private String type;

    @Column(name = "relation_id")
    private Long relationId;

    public Long getId() {
        return id;
    }

    public Long getSize() {
        return size;
    }

    public String getFilename() {
        return filename;
    }

    public Long getRelationId() {
        return relationId;
    }

    public String getType() {
        return type;
    }


}
