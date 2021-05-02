/*
package com.sms.tests.grades;

import com.sms.clients.WebClient;
import com.sms.grades.GradeDTO;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import javax.ws.rs.core.MediaType;
import java.math.BigDecimal;

public class DeleteGradeTest {

    private final static WebClient ADMINCLIENT = new WebClient("smsadmin", "smsadmin");
    private final static WebClient TEACHERCLIENT = new WebClient("T_82734927389", "teacher");
    private final static String TESTBACKENDUSER = "a43856df-96bf-4747-b947-0b2b127ae677";


    @Test
    void shouldThrowExceptionOnInvalidId() {
        // DELETE GRADE
        TEACHERCLIENT.request("grades-service")
                .delete("/grades/delete/0")
                .then()
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @Test
    void shouldThrowExceptionOnInvalidRole() {
        // DELETE GRADE
        ADMINCLIENT.request("grades-service")
                .contentType(MediaType.APPLICATION_JSON)
                .delete("/grades/delete/0")
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void shouldDeleteUserGrade(){
        GradeDTO gradeDTO= buildGrade();
        createGrade(gradeDTO);
        //DELETE GRADE
        TEACHERCLIENT.request("grades-service")
                .contentType(MediaType.APPLICATION_JSON)
                .delete("/grades/delete/" + gradeDTO.getId())
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

    }

    @Test
    void shouldDeleteAllUserGradesEndpoint(){
        GradeDTO gradeDTO= buildGrade();
        createGrade(gradeDTO);
        gradeDTO= buildGrade();
        createGrade(gradeDTO);

        //DELETE GRADE
        ADMINCLIENT.request("grades-service")
                .contentType(MediaType.APPLICATION_JSON)
                .delete("/grades/" + TESTBACKENDUSER)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    void shouldDeleteAllUserGrades(){
        GradeDTO gradeDTO= buildGrade();
        createGrade(gradeDTO);
        gradeDTO= buildGrade();
        createGrade(gradeDTO);

        //DELETE GRADE
        ADMINCLIENT.request("usermanagement-service")
                .contentType(MediaType.APPLICATION_JSON)
                .delete("/users/" + TESTBACKENDUSER)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    private GradeDTO buildGrade(){
        return GradeDTO.builder()
                .grade(BigDecimal.valueOf(5))
                .description("test")
                .isFinal(false)
                .subject("Math")
                .weight(1)
                .teacherId("d2364974-cfa8-45c0-b133-57df2c89a327")
                .studentId("a43856df-96bf-4747-b947-0b2b127ae677")
                .build();
    }

    private void createGrade(GradeDTO gradeDTO){
         TEACHERCLIENT.request("grades-service")
                .contentType(MediaType.APPLICATION_JSON)
                .body(gradeDTO)
                .put("/grades")
                .then()
                .statusCode(HttpStatus.OK.value());
    }

}
*/
