package com.sms.model.homework;

import com.sms.api.homework.FileLinkDTO;

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

    public void setType(FileLinkDTO.Type type) {
        this.type = type.toString();
    }

    public void setRelationId(Long relationId) {
        this.relationId = relationId;
    }

    private String type;

    @Column(name = "relation_id")
    private Long relationId;

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    @Column(name= "owner_id")
    private String ownerId;


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

    public FileLinkDTO.Type getType() {
        return FileLinkDTO.Type.valueOf(type);
    }


}
