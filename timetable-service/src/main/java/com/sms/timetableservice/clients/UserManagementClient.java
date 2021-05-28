package com.sms.timetableservice.clients;

import com.sms.api.usermanagement.UserDTO;
import com.sms.clients.ServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;

@Component
@Scope("prototype")
public class UserManagementClient {

    private static final String USERMANAGEMENT = "usermanagement-service";

    @Autowired
    ServiceClient serviceClient;

    public Optional<UserDTO> getUser(String userId) {
        Response response = serviceClient.target(USERMANAGEMENT)
                .path("users")
                .path(userId)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get();
        if (response.getStatus() == HttpStatus.OK.value()) {
            return Optional.of(response.readEntity(UserDTO.class));
        } else {
            return Optional.empty();
        }
    }

    public Set<String> getGroups() {
        Response response = serviceClient.target(USERMANAGEMENT)
                .path("groups")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get();
        if (response.getStatus() == HttpStatus.OK.value()) {
            return response.readEntity(new GenericType<Set<String>>() {});
        } else {
            return Collections.emptySet();
        }
    }

    public Set<String> getSubjects() {
        Response response = serviceClient.target(USERMANAGEMENT)
                .path("subjects")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get();
        if (response.getStatus() == HttpStatus.OK.value()) {
            return response.readEntity(new GenericType<Set<String>>() {});
        } else {
            return Collections.emptySet();
        }
    }

    public List<UserDTO> getUsers(Set<String> ids) {
        ServiceClient.ServiceTarget target = serviceClient.target(USERMANAGEMENT)
                .path("users")
                .path("ids");
        ids.forEach(id -> target.queryParam("id", id));

        Response response = target.request(MediaType.APPLICATION_JSON_TYPE)
                .get();
        if (response.getStatus() == HttpStatus.OK.value()) {
            return Arrays.asList(response.readEntity(UserDTO[].class));
        } else {
            return Collections.emptyList();
        }
    }
}
