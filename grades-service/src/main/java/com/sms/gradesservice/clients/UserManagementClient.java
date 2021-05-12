package com.sms.gradesservice.clients;

import com.sms.clients.ServiceClient;
import com.sms.api.usermanagement.UserDTO;
import com.sms.api.usermanagement.UsersFiltersDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
@Scope("prototype")
public class UserManagementClient {

    private static final String USERMANAGEMENT = "usermanagement-service";

    @Autowired
    ServiceClient serviceClient;

    public List<UserDTO> getUsers(UsersFiltersDTO filters) {
        Response response = serviceClient.target(USERMANAGEMENT)
                .path("users")
                .path("filter")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(filters, MediaType.APPLICATION_JSON));
        if (response.getStatus() == HttpStatus.OK.value()) {
            return Arrays.asList(response.readEntity(UserDTO[].class));
        } else {
            return Collections.emptyList();
        }
    }

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
}
