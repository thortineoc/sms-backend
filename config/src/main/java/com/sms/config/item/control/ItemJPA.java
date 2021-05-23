package com.sms.config.item.control;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "item")
public class ItemJPA {

    private String key;
    private byte[] value;

    @Id
    @Column(name = "key")
    public String getKey() {
        return key;
    }

    @Column(name = "value")
    public byte[] getValue() {
        return value;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setValue(byte[] value) {
        this.value = value;
    }
}
