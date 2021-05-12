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

    public static final String USERMANAGEMENT = "usermanagement-service";
    public static final String HOMEWORK = "homework-service";
    public static final String PRESENCE = "presence-service";
    public static final String TIMETABLE = "timetable-service";
    public static final String GRADES = "grades-service";

    public final Map<String, String> localServices = new HashMap<String, String>() {{
     //  put(HOMEWORK, "http://localhost:24026");
     //  put(PRESENCE, "http://localhost:24028");
     //  put(TIMETABLE, "http://localhost:24030");
        put(USERMANAGEMENT, "http://localhost:24034");
        put(GRADES, "http://localhost:24032");
    }};

    Environment() {
    }
}
