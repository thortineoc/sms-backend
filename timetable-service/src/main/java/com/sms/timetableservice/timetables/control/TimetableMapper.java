package com.sms.timetableservice.timetables.control;

import com.sms.api.common.Util;
import com.sms.api.timetables.LessonDTO;
import com.sms.api.timetables.TimetableDTO;
import com.sms.api.usermanagement.UserDTO;
import com.sms.timetableservice.timetables.entity.ClassJPA;

import java.util.*;
import java.util.stream.Collectors;

public class TimetableMapper {

    private TimetableMapper() {}

//    public static TimetableDTO toDTO(List<ClassJPA> classes, Map<Long, ClassJPA> conflicts, UserDTO teacher) {
//        Map<Integer, List<LessonDTO>> lessonsByWeekday = classes.stream()
//                .map(c -> toDTO(c, conflicts, teacher))
//                .sorted(Comparator.comparing(LessonDTO::getWeekDay))
//                .collect(Collectors.groupingBy(LessonDTO::getWeekDay, LinkedHashMap::new, Collectors.toList()));
//
//        List<List<LessonDTO>> lessons = new ArrayList<>(lessonsByWeekday.values());
//
//        return TimetableDTO.builder()
//                .lessons(lessons)
//                .build();
//    }

    public static TimetableDTO toDTO(List<ClassJPA> classes, Map<String, UserDTO> teachers) {
        Map<Integer, List<LessonDTO>> lessonsByWeekday = classes.stream()
                .map(c -> toDTO(c, teachers.get(c.getTeacherId())))
                .sorted(Comparator.comparing(LessonDTO::getWeekDay))
                .collect(Collectors.groupingBy(LessonDTO::getWeekDay, LinkedHashMap::new, Collectors.toList()));

        List<List<LessonDTO>> lessons = new ArrayList<>(lessonsByWeekday.values());

        return TimetableDTO.builder()
                .lessons(lessons)
                .build();
    }

//    public static LessonDTO toDTO(ClassJPA jpa, Map<Long, ClassJPA> conflict, UserDTO teacher) {
//        return LessonDTO.builder()
//                .conflict(Util.getOpt(conflict, jpa.getId()).map(c -> toDTO(c, teacher)))
//                .group(jpa.getGroup())
//                .lesson(jpa.getLesson())
//                .subject(jpa.getSubject())
//                .teacher(teacher)
//                .weekDay(jpa.getWeekday())
//                .build();
//    }

    public static LessonDTO toDTO(ClassJPA jpa, UserDTO teacher) {
        return LessonDTO.builder()
                .group(jpa.getGroup())
                .lesson(jpa.getLesson())
                .subject(jpa.getSubject())
                .teacher(teacher)
                .weekDay(jpa.getWeekday())
                .build();
    }
}
