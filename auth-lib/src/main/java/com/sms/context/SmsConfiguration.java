package com.sms.context;

import java.io.IOException;
import java.util.Properties;

public class SmsConfiguration {

    private static final String CONFIG_FILE = "auth-lib.properties";

    // HAProxy
    public static final String webHost;
    public static final String webPort;
    public static final String haproxyUrl;

    // Keycloak
    public static final String adminUsername;
    public static final String adminPassword;
    public static final String realmName;
    public static final String realmClient;
    public static final String adminClient;

    static {
        Properties props = new Properties();
        try {
            props.load(SmsConfiguration.class.getClassLoader().getResourceAsStream(CONFIG_FILE));
        } catch (IOException e) {
            e.printStackTrace();
        }

        webHost = props.getProperty("webHost", "localhost");
        webPort = props.getProperty("webPort", "24020");
        haproxyUrl = props.getProperty("haproxyUrl", "http://" + webHost + ":" + webPort);
        adminUsername = props.getProperty("adminUsername", "kcuser");
        adminPassword = props.getProperty("adminPassword", "kcuser");
        realmName = props.getProperty("realmName", "sms");
        adminClient = props.getProperty("adminClient", "admin-cli");
        realmClient = props.getProperty("realmClient", "frontend");
    }
}
