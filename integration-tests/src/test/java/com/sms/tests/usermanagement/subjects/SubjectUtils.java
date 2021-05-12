package com.sms.tests.usermanagement.subjects;

import com.sms.clients.WebClient;
import io.restassured.response.Response;

import javax.ws.rs.core.MediaType;

import static com.sms.tests.usermanagement.TestUtils.USER_MANAGEMENT;

public class SubjectUtils {

    private static final WebClient CLIENT = new WebClient("smsadmin", "smsadmin");

    public static Response getSubjects() {
        return CLIENT.request(USER_MANAGEMENT)
                .contentType(MediaType.APPLICATION_JSON)
                .log().all()
                .get("/subjects");
    }

    public static Response deleteSubject(String name) {
        String path = "/subjects/" + name;
        return CLIENT.request(USER_MANAGEMENT)
                .log().all()
                .delete(path);
    }

    public static Response createSubject(String name) {
        String path = "/subjects/" + name;
        return CLIENT.request(USER_MANAGEMENT)
                .log().all()
                .post(path);
    }
}
