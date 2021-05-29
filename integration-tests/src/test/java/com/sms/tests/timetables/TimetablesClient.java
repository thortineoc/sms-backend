package com.sms.tests.timetables;

import com.sms.api.usermanagement.UserDTO;
import com.sms.clients.WebClient;
import io.restassured.specification.RequestSpecification;

import javax.ws.rs.core.MediaType;

import static com.sms.clients.Environment.TIMETABLE;

public abstract class TimetablesClient {

    private final WebClient client;
    private final UserDTO user;

    protected TimetablesClient() {
        this.client = new WebClient("smsadmin", "smsadmin");
        this.user = null;
    }

    protected TimetablesClient(UserDTO user) {
        String password = user.getFirstName().substring(0, Math.min(4, user.getFirstName().length()))
                + user.getLastName().substring(0, Math.min(user.getLastName().length(), 4));
        this.client = new WebClient(user.getUserName(), password);
        this.user = user;
    }

    public RequestSpecification getRequest() {
        return client.request(TIMETABLE)
                .contentType(MediaType.APPLICATION_JSON)
                .log().all();
    }

    public String getId() {
        return user.getId();
    }

    public UserDTO getUser() {
        return user;
    }
}
