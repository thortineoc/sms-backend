package com.sms.model.homework;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileDetailJPA{

    byte[] getFile();
    void setFile(MultipartFile file) throws IOException;

}
