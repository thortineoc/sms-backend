package com.sms.clients;


import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public enum Environment {
    ENV;

    public final String authClientId = "frontend";
    public final String authClientSecret = UUID.randomUUID().toString();

    public final String testUsername = "testbackenduser";
    public final String testUserPassword = "testbackenduser";

    public final String haproxyUrl = "http://52.142.201.18:24020";
    public final String tokenUrl = "/auth/realms/sms/protocol/openid-connect/token";

    public final Map<String, String> localServices = new HashMap<String, String>() {{
     //  put("homework-service", "http://localhost:24026");
     //  put("presence-service", "http://localhost:24028");
     //  put("timetable-service", "http://localhost:24030");
//       put("usermanagement-service", "http://localhost:24034");
     //  put("grades-service", "http://localhost:24032");
    }};

    Environment() {
    }
}
