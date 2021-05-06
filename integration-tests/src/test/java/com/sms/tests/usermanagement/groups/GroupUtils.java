package com.sms.tests.usermanagement.groups;

import com.sms.clients.Environment;
import com.sms.clients.WebClient;
import io.restassured.response.Response;

import javax.ws.rs.core.MediaType;

public class GroupUtils {

    private static final WebClient CLIENT = new WebClient("smsadmin", "smsadmin");

    public static Response getGroups() {
        return CLIENT.request(Environment.USERMANAGEMENT)
                .contentType(MediaType.APPLICATION_JSON)
                .log().all()
                .get("/groups");
    }

    public static Response deleteGroup(WebClient client, String name) {
        return client.request(Environment.USERMANAGEMENT)
                .contentType(MediaType.APPLICATION_JSON)
                .log().all()
                .delete("/groups/" + name);
    }

    public static Response deleteGroup(String name) {
        return deleteGroup(CLIENT, name);
    }

    public static Response createGroup(WebClient client, String name) {
        return client.request(Environment.USERMANAGEMENT)
                .log().all()
                .post("/groups/" + name);
    }

    public static Response createGroup(String name) {
        return createGroup(CLIENT, name);
    }
}
