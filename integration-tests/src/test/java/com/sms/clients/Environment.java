package com.sms.clients;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

public class Environment {

    public static final String authClientSecret = UUID.randomUUID().toString();

    public static String testUsername;
    public static String testUserPassword;
    public static String realmClient = "frontend";

    public static String haproxyUrl;
    public static String tokenUrl;
    public static String realmName;
    public static String adminClient;
    public static String adminUsername;
    public static String adminPassword;


    public static final String USERMANAGEMENT = "usermanagement-service";
    public static final String HOMEWORK = "homework-service";
    public static final String PRESENCE = "presence-service";
    public static final String TIMETABLE = "timetable-service";
    public static final String GRADES = "grades-service";

    private static final String CONFIG_FILE = "tests.properties";

    static {
        Properties props = new Properties();
        try {
            props.load(Environment.class.getClassLoader().getResourceAsStream(CONFIG_FILE));
        } catch (IOException e) {
            throw new IllegalStateException("Failed loading config file: " + CONFIG_FILE + ", reason: " + e);
        }
        Environment.haproxyUrl = props.getProperty("haproxyUrl", "http://52.142.201.18:24020");
        Environment.tokenUrl = props.getProperty("tokenUrl", "/auth/realms/sms/protocol/openid-connect/token");
        Environment.testUsername = props.getProperty("testUsername", "testbackenduser");
        Environment.testUserPassword = props.getProperty("testUserPassword", "testbackenduser");
        Environment.adminClient = props.getProperty("adminClient", "admin-cli");
        Environment.realmClient = props.getProperty("realmClient", "frontend");
        Environment.realmName = props.getProperty("realmName", "sms");
        Environment.adminUsername = props.getProperty("adminUsername", "kcuser");
        Environment.adminPassword = props.getProperty("adminPassword", "kcuser");
    }

    public static final Map<String, String> LOCAL_SERVICES = new HashMap<String, String>() {{
//       put(HOMEWORK, "http://localhost:24026");
//       put(PRESENCE, "http://localhost:24028");
       put(TIMETABLE, "http://localhost:24030");
       put(USERMANAGEMENT, "http://localhost:24034");
//       put(GRADES, "http://localhost:24032");
    }};
}
