package com.sms.context;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Properties;

@Component
public class SmsConfiguration {

    private static final String CONFIG_FILE = "application.properties";

    // HAProxy
    private String webHost;
    private String webPort;
    private String haproxyUrl;

    // Keycloak
    private String adminUsername;
    private String adminPassword;
    private String realmName;
    private String realmClient;
    private String adminClient;

    // If this throws an error on build or runtime, make sure application.properties exists
    @PostConstruct
    private void init() throws IOException {
        Properties props = new Properties();
        props.load(SmsConfiguration.class.getClassLoader().getResourceAsStream(CONFIG_FILE));

        webHost = props.getProperty("webHost", "52.142.201.18");
        webPort = props.getProperty("webPort", "24020");
        haproxyUrl = props.getProperty("haproxyUrl", "http://" + webHost + ":" + webPort);
        adminUsername = props.getProperty("adminUsername", "kcuser");
        adminPassword = props.getProperty("adminPassword", "kcuser");
        realmName = props.getProperty("realmName", "sms");
        adminClient = props.getProperty("adminClient", "admin-cli");
        realmClient = props.getProperty("realmClient", "frontend");
    }

    public String getWebHost() {
        return webHost;
    }

    public String getWebPort() {
        return webPort;
    }

    public String getAdminClient() {
        return adminClient;
    }

    public String getRealmClient() {
        return realmClient;
    }

    public String getAdminPassword() {
        return adminPassword;
    }

    public String getAdminUsername() {
        return adminUsername;
    }

    public String getHaproxyUrl() {
        return haproxyUrl;
    }

    public String getRealmName() {
        return realmName;
    }
}
