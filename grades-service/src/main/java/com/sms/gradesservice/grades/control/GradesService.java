package com.sms.gradesservice.grades.control;

import com.sms.context.UserContext;
import com.sms.grades.GradeDTO;
import com.sms.gradesservice.grades.control.repository.GradesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.EntityNotFoundException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Scope("request")
public class GradesService {

    @Autowired
    UserContext userContext;

    @Autowired
    GradesRepository gradesRepository;

    public Map<String, List<GradeDTO>> getStudentGrades() {
        String studentId = userContext.getUserId();
        try {
            return gradesRepository.findAllByStudentId(studentId)
                    .stream()
                    .map(GradesMapper::toDTO)
                    .collect(Collectors.groupingBy(GradeDTO::getSubject));
        } catch (EntityNotFoundException e) {
            return Collections.emptyMap();
        }
    }

    public Map<String, List<GradeDTO>> getTeacherGrades(String subject, List<String> studentIds) {
        try {
            return gradesRepository.findAllBySubjectAndStudentIdIn(subject, studentIds)
                    .stream()
                    .map(GradesMapper::toDTO)
                    .collect(Collectors.groupingBy(GradeDTO::getStudentId));
        } catch (EntityNotFoundException e) {
            return Collections.emptyMap();
        }
    }
}
