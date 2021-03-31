package com.sms.homeworkservice;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.core.Response;

@RestController
public class HealthResource {

    @GetMapping("/health")
    public Response health() {
        return Response.ok().build();
    }
}
