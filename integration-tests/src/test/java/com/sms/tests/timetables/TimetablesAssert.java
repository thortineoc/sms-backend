package com.sms.tests.timetables;

import com.sms.api.timetables.TimetableDTO;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;

public class TimetablesAssert {

    private final Response response;
    private TimetableDTO timetable;

    public TimetablesAssert(Response response) {
        this.response = response;
    }

    public TimetablesAssert unwrapTimetable() {
        response.then().statusCode(200);
        timetable = response.as(TimetableDTO.class);
        return this;
    }

//    public TimetablesAssert assertLayout(byte[][] expected) {
//        for (int lesson = 0; lesson < expected.length; lesson++) {
//            for (int day = 0; day < 5; day++) {
//                boolean isPresent = layout[lesson][day] == 1;
//                if (isPresent) {
//                    Assertions.assertTrue(classes.containsKey(new LessonKey(day, lesson)));
//                } else {
//                    Assertions.assertFalse(classes.containsKey(new LessonKey(day, lesson)));
//                }
//            }
//        } TODO
//    }
}
