package com.sms.clients.entity;

import javax.ws.rs.client.WebTarget;
import java.util.HashMap;
import java.util.Map;

public class UserSearchParams {

    private final Map<String, Object> params = new HashMap<>();

    public Boolean getBriefRepresentation() {
        return (Boolean) params.get("briefRepresentation");
    }

    public String getEmail() {
        return (String) params.get("email");
    }

    public Integer getFirst() {
        return (Integer) params.get("first");
    }

    public String getFirstName() {
        return (String) params.get("firstName");
    }

    public String getLastName() {
        return (String) params.get("lastName");
    }

    public Integer getMax() {
        return (Integer) params.get("max");
    }

    public String getSearch() {
        return (String) params.get("search");
    }

    public String getUsername() {
        return (String) params.get("username");
    }

    public UserSearchParams briefRepresentation(Boolean value) {
        params.put("briefRepresentation", value);
        return this;
    }

    public UserSearchParams email(String value) {
        params.put("email", value);
        return this;
    }

    public UserSearchParams first(Integer value) {
        params.put("first", value);
        return this;
    }

    public UserSearchParams firstName(String value) {
        params.put("firstName", value);
        return this;
    }

    public UserSearchParams lastName(String value) {
        params.put("lastName", value);
        return this;
    }

    public UserSearchParams max(Integer value) {
        params.put("max", value);
        return this;
    }

    public UserSearchParams search(String value) {
        params.put("search", value);
        return this;
    }

    public UserSearchParams username(String value) {
        params.put("username", value);
        return this;
    }

    public WebTarget addParams(WebTarget target) {
        for (Map.Entry<String, Object> param : params.entrySet()) {
            target = target.queryParam(param.getKey(), param.getValue());
        }
        return target;
    }
}
