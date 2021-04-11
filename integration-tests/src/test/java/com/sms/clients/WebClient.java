package com.sms.clients;

import com.google.common.base.Stopwatch;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.concurrent.TimeUnit;


public class WebClient {

    private static final Environment ENV = Environment.ENV;

    private static String AUTH_TOKEN;
    private static String REFRESH_TOKEN;
    private static Integer AUTH_EXPIRES_IN;
    private static Integer REFRESH_EXPIRES_IN;
    private static final Stopwatch TOKEN_EXPIRATION_TIMER = Stopwatch.createUnstarted();

    private final String username;
    private final String password;

    public WebClient() {
        this.username = ENV.testUsername;
        this.password = ENV.testUserPassword;
    }

    public WebClient(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public RequestSpecification request() {
        if (AUTH_TOKEN == null) {
            getAccessToken();
        } else if (TOKEN_EXPIRATION_TIMER.elapsed(TimeUnit.SECONDS) > AUTH_EXPIRES_IN) {
            refreshToken();
        }

        return RestAssured.given()
                .auth().oauth2(AUTH_TOKEN)
                .when();
    }

    public RequestSpecification request(String serviceName) {
        return request()
                .baseUri(ENV.localServices.getOrDefault(serviceName, ENV.haproxyUrl))
                .basePath(serviceName);
    }

    private void refreshToken() {
        Response response = RestAssured.given()
                .auth().preemptive()
                .basic(ENV.authClientId, ENV.authClientSecret)
                .contentType("application/x-www-form-urlencoded").log().all()
                .formParam("client_id", ENV.authClientId)
                .formParam("grant_type", "refresh_token")
                .formParam("refresh_token", REFRESH_TOKEN)
                .when()
                .post(ENV.haproxyUrl + ENV.tokenUrl);
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
                .basic(ENV.authClientId, ENV.authClientSecret)
                .contentType("application/x-www-form-urlencoded").log().all()
                .formParam("grant_type", "password")
                .formParam("username", username)
                .formParam("password", password)
                .when()
                .post(ENV.haproxyUrl + ENV.tokenUrl);

        parseTokenResponse(response.getBody().jsonPath());
        resetTimer();
    }

    private void resetTimer() {
        TOKEN_EXPIRATION_TIMER.reset();
        TOKEN_EXPIRATION_TIMER.start();
    }

    private void parseTokenResponse(JsonPath json) {
        AUTH_TOKEN = json.get("access_token");
        REFRESH_TOKEN = json.get("refresh_token");
        AUTH_EXPIRES_IN = json.get("expires_in");
        REFRESH_EXPIRES_IN = json.get("refresh_expires_in");
    }
}
