package com.sms.tests.homework;

import com.sms.clients.Environment;
import com.sms.clients.WebClient;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import javax.ws.rs.core.MediaType;

public class AdminHomeworkClient {

    private final WebClient client = new WebClient("smsadmin", "smsadmin");

    public Response deleteHomework(Long homeworkId) {
        return getRequest().delete("/homework/" + homeworkId);
    }

    public Response deleteAnswer(Long answerId) {
        return getRequest().delete("/answer/" + answerId);
    }

    public Response deleteFile(Long fileId) {
        return getRequest().delete("/files/" + fileId);
    }

    private RequestSpecification getRequest() {
        return client.request(Environment.HOMEWORK)
                .contentType(MediaType.APPLICATION_JSON)
                .log().all();
    }
}
