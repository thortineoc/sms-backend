package com.sms.usermanagementservice.clients;

import com.sms.clients.ServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


@Component
@Scope("prototype")
public class GradesClient {

    @Autowired
    ServiceClient serviceClient;

    private static final String GRADES = "grades-service";

    public boolean deleteGrades(String id) {
        Response response = serviceClient.overrideHaproxyUrl("http://localhost:24032").target(GRADES) // add ` overrideHaproxyUrl("http://localhost:24032") `
                .path("grades")    // to test locally
                .path("user")
                .path(id)
                .request(MediaType.TEXT_PLAIN_TYPE)
                .delete();

        return response.getStatus() == HttpStatus.NO_CONTENT.value();
    }

}
