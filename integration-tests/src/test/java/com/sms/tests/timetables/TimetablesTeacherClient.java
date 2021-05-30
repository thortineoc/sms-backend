package com.sms.tests.timetables;

import com.sms.api.usermanagement.UserDTO;
import io.restassured.response.Response;

public class TimetablesTeacherClient extends TimetablesClient {

    public TimetablesTeacherClient(UserDTO user) {
        super(user);
    }

    public Response getTimetable() {
        return getRequest().get("/timetables/teacher");
    }
}
