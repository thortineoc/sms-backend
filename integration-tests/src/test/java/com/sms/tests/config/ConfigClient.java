package com.sms.tests.config;

import com.sms.api.timetables.TimetableConfigDTO;
import com.sms.clients.Environment;
import com.sms.clients.WebClient;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import javax.ws.rs.core.MediaType;

public class ConfigClient {

    private final WebClient admin = new WebClient("smsadmin", "smsadmin");

    public Response deleteConfig() {
        return getRequest().delete("/config");
    }

    public Response saveConfig(TimetableConfigDTO config) {
        return getRequest().body(config).post("/config");
    }

    public Response getConfig() {
        return getRequest().get("/config");
    }

    public RequestSpecification getRequest() {
        return admin.request(Environment.TIMETABLE)
                .contentType(MediaType.APPLICATION_JSON)
                .log().all();
    }
}
