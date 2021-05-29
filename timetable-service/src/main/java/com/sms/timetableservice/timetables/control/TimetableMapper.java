package com.sms.timetableservice.timetables.control;

import com.sms.api.common.Util;
import com.sms.api.timetables.LessonDTO;
import com.sms.api.timetables.TimetableDTO;
import com.sms.api.usermanagement.UserDTO;
import com.sms.timetableservice.timetables.entity.ClassJPA;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class TimetableMapper {

    private TimetableMapper() {}

    public static TimetableDTO toDTO(List<ClassJPA> classes, Map<String, UserDTO> teachers, Map<Long, ClassJPA> conflicts) {
        Map<Integer, List<LessonDTO>> lessonsByWeekday = classes.stream()
                .map(c -> toDTO(c, conflicts))
                .sorted(Comparator.comparing(LessonDTO::getWeekDay))
                .collect(Collectors.groupingBy(LessonDTO::getWeekDay, LinkedHashMap::new,
                        Util.collectSorted(Comparator.comparing(LessonDTO::getLesson))));
        List<List<LessonDTO>> lessons = fillInEmptyLessons(lessonsByWeekday);

        return TimetableDTO.builder()
                .lessons(lessons)
                .teachers(teachers)
                .build();
    }

    public static LessonDTO toDTO(ClassJPA jpa, Map<Long, ClassJPA> conflicts) {
        List<LessonDTO> realConflicts = getConflictIds(jpa).stream()
                .map(id -> Util.getOrThrow(conflicts, id,
                        () -> new IllegalStateException("Conflicting class with id: " + id + " doesn't exist")))
                .map(TimetableMapper::toDTO)
                .collect(Collectors.toList());

        return LessonDTO.builder()
                .id(jpa.getId())
                .group(jpa.getGroup())
                .lesson(jpa.getLesson())
                .subject(jpa.getSubject())
                .teacherId(jpa.getTeacherId())
                .conflicts(realConflicts)
                .room(Optional.ofNullable(jpa.getRoom()))
                .weekDay(jpa.getWeekday())
                .build();
    }

    public static TimetableDTO toDTO(List<ClassJPA> classes, Map<String, UserDTO> teachers) {
        Map<Integer, List<LessonDTO>> lessonsByWeekday = classes.stream()
                .map(TimetableMapper::toDTO)
                .sorted(Comparator.comparing(LessonDTO::getWeekDay))
                .collect(Collectors.groupingBy(LessonDTO::getWeekDay, LinkedHashMap::new,
                        Util.collectSorted(Comparator.comparing(LessonDTO::getLesson))));
        List<List<LessonDTO>> lessons = fillInEmptyLessons(lessonsByWeekday);

        return TimetableDTO.builder()
                .lessons(lessons)
                .teachers(teachers)
                .build();
    }

    private static List<Long> getConflictIds(ClassJPA classJPA) {
        return Optional.ofNullable(classJPA.getConflicts())
                .map(c -> Arrays.stream(c.split(","))
                    .map(Long::valueOf)
                    .collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }

    private static List<List<LessonDTO>> fillInEmptyLessons(Map<Integer, List<LessonDTO>> lessons) {
        for (int day = 0; day < 5; day++) {
            if (!lessons.containsKey(day)) {
                lessons.put(day, Collections.emptyList());
            }
        }
        return lessons.values().stream()
                .filter(e -> !e.isEmpty())
                .map(dailyLessons -> Stream.concat(
                        IntStream.range(0, findMinLesson(dailyLessons)).mapToObj(i -> null),
                        dailyLessons.stream()
                ).collect(Collectors.toList()))
                .collect(Collectors.toList());
    }

    private static int findMinLesson(List<LessonDTO> lessons) {
        return lessons.stream()
                .min(Comparator.comparing(LessonDTO::getLesson))
                .map(LessonDTO::getLesson)
                .orElseThrow(() -> new IllegalStateException("None of the lessons: " + lessons + " had a lesson number."));
    }

    public static LessonDTO toDTO(ClassJPA jpa) {
        return LessonDTO.builder()
                .id(jpa.getId())
                .group(jpa.getGroup())
                .lesson(jpa.getLesson())
                .subject(jpa.getSubject())
                .teacherId(jpa.getTeacherId())
                .room(Optional.ofNullable(jpa.getRoom()))
                .weekDay(jpa.getWeekday())
                .build();
    }
}
