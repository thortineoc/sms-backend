package com.sms.timetableservice.timetables.control;

import com.sms.api.common.Util;
import com.sms.api.timetables.LessonDTO;
import com.sms.api.timetables.LessonsDTO;
import com.sms.api.timetables.TimetableDTO;
import com.sms.api.usermanagement.UserDTO;
import com.sms.timetableservice.timetables.entity.ClassJPA;
import com.sms.timetableservice.timetables.entity.Lesson;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class TimetableMapper {

    private TimetableMapper() {}

    public static List<Lesson> toLessons(Collection<ClassJPA> classes) {
        return classes.stream()
                .map(Lesson::new)
                .collect(Collectors.toList());
    }

    public static List<ClassJPA> toJPAs(Collection<Lesson> lessons) {
        return lessons.stream()
                .map(Lesson::toJPA)
                .collect(Collectors.toList());
    }

    public static Map<Long, Lesson> toLessonsById(List<ClassJPA> classes) {
        return classes.stream()
                .map(Lesson::new)
                .collect(Collectors.toMap(Lesson::getId, Function.identity()));
    }

    public static TimetableDTO toDTO(List<Lesson> lessons, Map<String, UserDTO> teachers, Map<Long, ClassJPA> conflicts) {
        Map<Integer, List<LessonDTO>> lessonsByWeekday = lessons.stream()
                .map(c -> toDTO(c, conflicts))
                .sorted(Comparator.comparing(LessonDTO::getWeekday))
                .collect(Collectors.groupingBy(LessonDTO::getWeekday, LinkedHashMap::new,
                        Util.collectSorted(Comparator.comparing(LessonDTO::getLesson))));
        List<List<LessonDTO>> filledList = fillInEmptyLessons(lessonsByWeekday);

        return TimetableDTO.builder()
                .lessons(filledList)
                .teachers(teachers)
                .build();
    }

    public static LessonDTO toDTO(Lesson lesson, Map<Long, ClassJPA> conflicts) {
        List<LessonDTO> realConflicts = Util.getAll(conflicts, lesson.getConflicts()).stream()
                .map(TimetableMapper::toDTO)
                .collect(Collectors.toList());

        return LessonDTO.builder()
                .id(lesson.getId())
                .group(lesson.getGroup())
                .lesson(lesson.getKey().getLesson())
                .subject(lesson.getSubject())
                .teacherId(lesson.getTeacherId())
                .conflicts(realConflicts)
                .room(Optional.ofNullable(lesson.getRoom()))
                .weekday(lesson.getKey().getWeekday())
                .build();
    }

    public static TimetableDTO toDTO(List<ClassJPA> classes, Map<String, UserDTO> teachers) {
        Map<Integer, List<LessonDTO>> lessonsByWeekday = classes.stream()
                .map(TimetableMapper::toDTO)
                .sorted(Comparator.comparing(LessonDTO::getWeekday))
                .collect(Collectors.groupingBy(LessonDTO::getWeekday, LinkedHashMap::new,
                        Util.collectSorted(Comparator.comparing(LessonDTO::getLesson))));
        List<List<LessonDTO>> lessons = fillInEmptyLessons(lessonsByWeekday);

        return TimetableDTO.builder()
                .lessons(lessons)
                .teachers(teachers)
                .build();
    }

    public static List<LessonDTO> toDTOs(List<ClassJPA> classes) {
        return classes.stream()
                .map(TimetableMapper::toDTO)
                .collect(Collectors.toList());
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
                .weekday(jpa.getWeekday())
                .build();
    }


    public static List<ClassJPA> toJPA(List<LessonDTO> list) {
        return list.stream()
                .map(TimetableMapper::toJPA)
                .collect(Collectors.toList());
    }

    public static ClassJPA toJPA(LessonDTO dto) {
        dto.getRoom();
        ClassJPA jpa = new ClassJPA();
        dto.getId().ifPresent(jpa::setId);
        jpa.setGroup(dto.getGroup());
        jpa.setTeacherId(dto.getTeacherId().get());
        jpa.setLesson(dto.getLesson());
        jpa.setRoom(dto.getRoom().get());
        jpa.setWeekday(dto.getWeekday());
        jpa.setSubject(dto.getSubject());
        return jpa;
    }

    public static LessonDTO toDto(ClassJPA jpa) {
        return LessonDTO.builder()
                .id(jpa.getId())
                .group(jpa.getGroup())
                .subject(jpa.getSubject())
                .lesson(jpa.getLesson())
                .room(jpa.getRoom())
                .weekday(jpa.getWeekday())
                .teacherId(jpa.getTeacherId())
                .build();
    }

}
