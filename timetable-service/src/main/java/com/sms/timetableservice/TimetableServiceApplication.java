package com.sms.timetableservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.sms")
public class TimetableServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TimetableServiceApplication.class, args);
    }

}
