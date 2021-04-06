package com.sms.clients;

import com.sms.context.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

@Component
@Scope("prototype")
public class ServiceClient {

    private String haproxyUrl;
    private final Client client = ClientBuilder.newClient();

    @Autowired
    UserContext userContext;

    public ServiceTarget target(String serviceName) {
        return new ServiceTarget(serviceName);
    }

    public ServiceClient haproxyUrl(String haproxyUrl) {
        this.haproxyUrl = haproxyUrl;
        return this;
    }

    public class ServiceTarget {
        private WebTarget target;

        ServiceTarget(String serviceName) {
            this.target = client.target(haproxyUrl).path(serviceName);
        }

        public WebTarget getWebTarget() {
            return target;
        }

        public ServiceTarget path(String path) {
            target = target.path(path);
            return this;
        }

        public ServiceTarget queryParam(String name, Object... values) {
            target = target.queryParam(name, values);
            return this;
        }

        public Invocation.Builder request(MediaType mediaType) {
            return this.target.request(mediaType).header("Authorization", "Bearer " + userContext.getToken());
        }
    }
}
