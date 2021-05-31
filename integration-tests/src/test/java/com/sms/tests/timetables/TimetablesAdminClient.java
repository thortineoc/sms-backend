package com.sms.tests.timetables;

import io.restassured.response.Response;

import java.util.Map;

public class TimetablesAdminClient extends TimetablesClient {

    protected TimetablesAdminClient() {
        super();
    }

    public Response getTimetableForGroup(String group) {
        return getRequest().get("/timetables/" + group);
    }

    public Response deleteTimetableForGroup(String group) {
        return getRequest().delete("/timetables/group/" + group);
    }

    public Response deleteClassesWithSubject(String subject) {
        return getRequest().delete("/timetables/subject/" + subject);
    }

    public Response deleteLesson(Long id) {
        return getRequest().delete("/timetables/id/" + id);
    }

    public Response generateTimetable(String group, Map<String, Map<String, Integer>> requestInfo) {
        return getRequest().body(requestInfo).post("/timetables/generate/" + group);
    }
}
