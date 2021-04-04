package com.sms.infrastructure;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;


public class WebClient {

    private static final Environment ENV = Environment.ENV;

    private final String authToken;
    private final String username;
    private final String password;

    public WebClient() {
        this.username = ENV.testUsername;
        this.password = ENV.testUserPassword;
        this.authToken = getAccessToken();
    }

    public WebClient(String username, String password) {
        this.username = username;
        this.password = password;
        this.authToken = getAccessToken();
    }

    public RequestSpecification request() {
        return RestAssured.given()
                .auth().oauth2(authToken)
                .when().baseUri(ENV.haproxyUrl);
    }

    public RequestSpecification request(String serviceName) {
        return request().basePath(serviceName);
    }

    public String getAccessToken() {
        Response response = RestAssured.given()
                .auth().preemptive()
                .basic(ENV.authClientId, ENV.authClientSecret)
                .contentType("application/x-www-form-urlencoded").log().all()
                .formParam("grant_type", "password")
                .formParam("username", username)
                .formParam("password", password)
                .when()
                .post(ENV.haproxyUrl + ENV.tokenUrl);

        return response.getBody().jsonPath().get("access_token").toString();
    }
}
