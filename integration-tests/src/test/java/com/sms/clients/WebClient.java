package com.sms.clients;

import com.google.common.base.Stopwatch;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.concurrent.TimeUnit;


public class WebClient {

    private String authToken;
    private String refreshToken;
    private Integer authExpiresIn;
    private Integer refreshExpiresIn;
    private final Stopwatch tokenExpirationTimer = Stopwatch.createUnstarted();

    private final String username;
    private final String password;

    public WebClient() {
        this.username = Environment.testUsername;
        this.password = Environment.testUserPassword;
    }

    public WebClient(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public RequestSpecification request() {
        if (authToken == null) {
            getAccessToken();
        } else if (tokenExpirationTimer.elapsed(TimeUnit.SECONDS) > authExpiresIn) {
            refreshToken();
        }

        return RestAssured.given()
                .auth().oauth2(authToken)
                .when();
    }

    public RequestSpecification request(String serviceName) {
        return request()
                .baseUri(Environment.LOCAL_SERVICES.getOrDefault(serviceName, Environment.haproxyUrl))
                .basePath(serviceName);
    }

    private void refreshToken() {
        Response response = RestAssured.given()
                .auth().preemptive()
                .basic(Environment.realmClient, Environment.authClientSecret)
                .contentType("application/x-www-form-urlencoded").log().all()
                .formParam("client_id", Environment.realmClient)
                .formParam("grant_type", "refresh_token")
                .formParam("refresh_token", refreshToken)
                .when()
                .post(Environment.haproxyUrl + Environment.tokenUrl);
        if (response.statusCode() == 400) {
            getAccessToken();
        } else {
            parseTokenResponse(response.getBody().jsonPath());
            resetTimer();
        }
    }

    private void getAccessToken() {
        Response response = RestAssured.given()
                .auth().preemptive()
                .basic(Environment.realmClient, Environment.authClientSecret)
                .contentType("application/x-www-form-urlencoded").log().all()
                .formParam("grant_type", "password")
                .formParam("username", username)
                .formParam("password", password)
                .when()
                .post(Environment.haproxyUrl + Environment.tokenUrl);

        parseTokenResponse(response.getBody().jsonPath());
        resetTimer();
    }

    private void resetTimer() {
        tokenExpirationTimer.reset();
        tokenExpirationTimer.start();
    }

    private void parseTokenResponse(JsonPath json) {
        authToken = json.get("access_token");
        refreshToken = json.get("refresh_token");
        authExpiresIn = json.get("expires_in");
        refreshExpiresIn = json.get("refresh_expires_in");
    }
}
