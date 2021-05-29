package com.sms.tests.timetables;

import com.sms.clients.WebClient;
import io.restassured.response.Response;

public class TimetablesStudentClient extends TimetablesClient {

    public TimetablesStudentClient(WebClient client) {
        super(client);
    }

    public Response getTimetable() {
        return getRequest().get("/timetables/student");
    }
}
