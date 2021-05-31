package com.sms.tests.homework;

import com.sms.api.usermanagement.UserDTO;
import io.restassured.response.Response;

public class StudentHomeworkClient extends HomeworkClient {

    public StudentHomeworkClient(UserDTO user) {
        super(user);
    }

    public Response createAnswer(Long homeworkId) {
        return getRequest().post("/answer/" + homeworkId);
    }

    public Response deleteAnswer(Long answerId) {
        return getRequest().delete("/answer/" + answerId);
    }

    public Response getHomeworks() {
        return getRequest().get("/homework/student");
    }
}
