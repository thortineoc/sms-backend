package com.sms.usermanagementservice.clients;

import com.sms.clients.ServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Component
@Scope("prototype")
public class TimetablesClient {

    private static final String TIMETABLES = "timetable-service";

    @Autowired
    ServiceClient serviceClient;

    public boolean deleteTimetable(String group) {
        Response response = serviceClient.target(TIMETABLES)
                .path("timetables")
                .path("group")
                .path(group)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .delete();
        return response.getStatus() == 204;
    }

    public boolean deleteLessonsBySubject(String subject) {
        Response response = serviceClient.target(TIMETABLES)
                .path("timetables")
                .path("subject")
                .path(subject)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .delete();
        return response.getStatus() == 204;
    }

    public boolean deleteLessonsByTeacherId(String teacherId) {
        Response response = serviceClient.overrideHaproxyUrl("http://localhost:24030").target(TIMETABLES)
                .path("timetables")
                .path("teacher")
                .path(teacherId)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .delete();
        return response.getStatus() == 204;
    }
}
