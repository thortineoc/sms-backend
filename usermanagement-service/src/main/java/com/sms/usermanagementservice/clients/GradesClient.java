package com.sms.usermanagementservice.clients;

import com.sms.clients.ServiceClient;
import com.sms.grades.GradeDTO;
import com.sms.usermanagement.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

//TODO
@Component
@Scope("request")
public class GradesClient {

    private static final String GRADES= "grades-service";

    @Autowired
    ServiceClient serviceClient;


    public int deleteGrades(String id) {
        UserDTO user=buildUser(id);
        Response response = serviceClient.target(GRADES)
                .path("grades")
                .path("delete")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(user, MediaType.APPLICATION_JSON));
        if (response.getStatus() == HttpStatus.OK.value()) {
            return HttpStatus.NO_CONTENT.value();
        } else {
            return HttpStatus.FORBIDDEN.value();
        }
    }

    private UserDTO buildUser(String id){
        return UserDTO.builder()
                .id(id)
                .build();
    }
}
