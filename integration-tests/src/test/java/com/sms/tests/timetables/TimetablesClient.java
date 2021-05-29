package com.sms.tests.timetables;

import com.sms.clients.WebClient;
import io.restassured.specification.RequestSpecification;

import javax.ws.rs.core.MediaType;

import static com.sms.clients.Environment.TIMETABLE;

public abstract class TimetablesClient {

    private final WebClient client;

    protected TimetablesClient(WebClient client) {
        this.client = client;
    }

    public RequestSpecification getRequest() {
        return client.request(TIMETABLE)
                .contentType(MediaType.APPLICATION_JSON)
                .log().all();
    }
}
