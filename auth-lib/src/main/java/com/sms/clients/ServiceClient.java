package com.sms.clients;

import com.sms.api.common.JDK8Mapper;
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
@Scope("request")
public class ServiceClient {

    private static final String DEFAULT_HAPROXY_URL = "http://52.142.201.18:24020";
    private static String HAPROXY_URL = DEFAULT_HAPROXY_URL;

    private final Client client = ClientBuilder.newClient().register(new JDK8Mapper().getProvider());

    @Autowired
    UserContext userContext;

    public ServiceTarget target(String serviceName) {
        return new ServiceTarget(serviceName);
    }

    public ServiceClient overrideHaproxyUrl(String haproxyUrl) {
        ServiceClient.HAPROXY_URL = haproxyUrl;
        return this;
    }

    public class ServiceTarget {
        private WebTarget target;

        ServiceTarget(String serviceName) {
            this.target = client.target(HAPROXY_URL).path(serviceName);
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
            // reset haproxy url after a single request
            HAPROXY_URL = DEFAULT_HAPROXY_URL;

            return this.target.request(mediaType).header("Authorization", "Bearer " + userContext.getToken());
        }
    }
}
