package com.sms.tests.homework;

import com.sms.api.homework.SimpleHomeworkDTO;
import com.sms.api.usermanagement.UserDTO;
import io.restassured.response.Response;

public class TeacherHomeworkClient extends HomeworkClient {

    public TeacherHomeworkClient(UserDTO user) {
        super(user);
    }

    public Response getHomeworks() {
        return getRequest().get("/homework/teacher");
    }

    public Response updateHomework(SimpleHomeworkDTO homework) {
        return getRequest().put("/homework", homework);
    }

    public Response deleteHomework(Long id) {
        return getRequest().delete("/homework/" + id);
    }
}
