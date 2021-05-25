package com.sms.tests.config;

import com.sms.api.timetables.LessonConfigDTO;
import com.sms.api.timetables.TimetableConfigDTO;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import java.time.LocalTime;
import java.util.Collections;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ConfigManagementTest {

    private static final ConfigClient CLIENT = new ConfigClient();

    private static TimetableConfigDTO createdConfig;
    private static TimetableConfigDTO oldConfig;

    @BeforeAll
    static void backup() {
        Response response = CLIENT.getConfig();
        if (response.statusCode() == 200) {
            oldConfig = response.as(TimetableConfigDTO.class);
        }
    }

    @AfterAll
    static void cleanup() {
        if (oldConfig == null) {
            CLIENT.deleteConfig();
        } else {
            CLIENT.saveConfig(oldConfig);
        }
    }

    @Order(1)
    @Test
    void shouldSaveConfiguration() {
        createdConfig = getConfig(3, "08:00", "08:45");

        CLIENT.saveConfig(createdConfig).then().statusCode(204);
    }

    @Order(2)
    @Test
    void shouldGetConfiguration() {
        Response response = CLIENT.getConfig();
        response.then().statusCode(200);

        TimetableConfigDTO config = response.as(TimetableConfigDTO.class);

        Assertions.assertEquals(createdConfig, config);
    }

    @Order(3)
    @Test
    void shouldDeleteConfiguration() {
        CLIENT.deleteConfig().then().statusCode(204);

        CLIENT.getConfig().then().statusCode(204);
    }

    @Order(4)
    @Test
    void shouldReturnNoContentOnNoConfigurationToDelete() {
        CLIENT.deleteConfig().then().statusCode(204);
    }

    @Order(5)
    @Test
    void shouldReturnNoContentWhenNoConfigIsFound() {
        CLIENT.getConfig().then().statusCode(204);
    }

    private TimetableConfigDTO getConfig(int lessonCount, String startTime, String endTime) {
        return TimetableConfigDTO.builder()
                .config(Collections.singletonList(
                        Collections.singletonList(LessonConfigDTO.builder()
                                .startTime(LocalTime.parse(startTime))
                                .endTime(LocalTime.parse(endTime))
                        .build())
                ))
                .lessonCount(lessonCount)
                .build();
    }
}
