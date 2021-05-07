package com.sms.homeworkservice.homeworks.control.response;

public class ResponseFile {

    private String name;
    private String url;
    private long size;
    private long fileID;

    public ResponseFile(String name, String url, long size, long id) {
        this.name = name;
        this.url = url;
        this.size = size;
        this.fileID = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public void setFileID(long id) {
        this.fileID = id;
    }

    public long getFileID() {
        return fileID;
    }
}

