package com.sms.tests.timetables;

import com.sms.clients.WebClient;
import io.restassured.response.Response;

public class TimetablesTeacherClient extends TimetablesClient {

    public TimetablesTeacherClient(WebClient client) {
        super(client);
    }

    public Response getTimetable() {
        return getRequest().get("/timetables/teacher");
    }
}
