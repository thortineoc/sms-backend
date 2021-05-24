package com.sms.config.item.entity;

import com.sms.api.common.JDK8Mapper;

import java.io.IOException;

public class Item {

    private static final JDK8Mapper mapper = new JDK8Mapper();

    private final byte[] content;

    public Item(byte[] content) {
        this.content = content;
    }

    public <T> T get(Class<T> type) throws IOException {
        return mapper.readValue(content, type);
    }
}
