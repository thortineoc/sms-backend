package com.sms.api.grades;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.sms.api.usermanagement.UserDTO;
import org.immutables.value.Value;

@Value.Immutable
@Value.Style(builder = "new")
@JsonSerialize(as = ImmutableStudentGradesDTO.class)
@JsonDeserialize(as = ImmutableStudentGradesDTO.class, builder = ImmutableStudentGradesDTO.Builder.class)
public interface StudentGradesDTO {

    static ImmutableStudentGradesDTO.Builder builder() {
        return new ImmutableStudentGradesDTO.Builder();
    }

    UserDTO getStudent();

    GradesDTO getGrades();
}

/*
* [
*      {
*          "student": {
*              ... entire UserDTO structure ...
*          },
*          "grades": [
*              {
*                  "grade": 4,
*                  "description": "optional nullable description",
*                  "weight": 1,
*                  "studentId": "493534-c9dv8-csc9fn3-dsafdf",
*                  "teacherId": "vf99vrek-fmvo3f-fdfsov-32rfd",
*                  "subject": "Maths",
*                  "id": 1000434
*              }
*          ]
*      }
* ]
* */