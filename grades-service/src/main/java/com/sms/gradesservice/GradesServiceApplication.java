package com.sms.gradesservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.sms")
public class GradesServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(GradesServiceApplication.class, args);
    }

}
