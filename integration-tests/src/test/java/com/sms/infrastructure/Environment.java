package com.sms.infrastructure;


public enum Environment {
    ENV;

    public final String authClientId = "frontend";
    public final String authClientSecret = "ec78c6bb-8339-4bed-9b1b-e973d27107dc";
    public final String testUsername = "testbackenduser";
    public final String testUserPassword = "testbackenduser";
    public final String haproxyUrl = "http://52.142.201.18:24020";
    public final String tokenUrl = "/auth/realms/sms/protocol/openid-connect/token";

    Environment() {
    }
}
