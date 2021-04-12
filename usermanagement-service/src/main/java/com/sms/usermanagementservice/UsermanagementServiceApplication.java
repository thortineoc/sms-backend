package com.sms.usermanagementservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.sms")
public class UsermanagementServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UsermanagementServiceApplication.class, args);
    }

}
