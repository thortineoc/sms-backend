package com.sms.model.homework;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class FileBaseJPA {

    @Id
    private Long id;

    private String filename;

    private Long size;

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
