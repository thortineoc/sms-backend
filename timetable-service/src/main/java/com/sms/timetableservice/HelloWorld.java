package com.sms.timetableservice;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorld {
    Logger logger = LoggerFactory.getLogger(HelloWorld.class);
    @Value("${server.port}")
    private String serverPort;

    public HelloWorld() {
    }

    @RequestMapping({"/hello"})
    public String hello() throws InterruptedException {
        this.logger.info("Hello api called at server with port: " + this.serverPort);
        Thread.sleep(50L);
        return "Hello World from " + this.serverPort + "!";
    }
}
