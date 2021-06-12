package com.sms.timetableservice.timetables.control;

import com.sms.api.common.Util;
import com.sms.api.timetables.LessonDTO;
import com.sms.api.timetables.TimetableDTO;
import com.sms.api.usermanagement.UserDTO;
import com.sms.timetableservice.timetables.entity.ClassJPA;
import com.sms.timetableservice.timetables.entity.Lesson;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

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
        List<LessonDTO> dtos = lessons.stream()
                .map(l -> toDTO(l, conflicts))
                .collect(Collectors.toList());

        return TimetableDTO.builder()
                .lessons(dtos)
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
        List<LessonDTO> dtos = classes.stream().map(TimetableMapper::toDTO).collect(Collectors.toList());
        return TimetableDTO.builder()
                .lessons(dtos)
                .teachers(teachers)
                .build();
    }

    public static List<LessonDTO> toDTOs(List<ClassJPA> classes) {
        return classes.stream()
                .map(TimetableMapper::toDTO)
                .collect(Collectors.toList());
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
        dto.getTeacherId().ifPresent(jpa::setTeacherId);
        dto.getRoom().ifPresent(jpa::setRoom);
        jpa.setGroup(dto.getGroup());
        jpa.setLesson(dto.getLesson());
        jpa.setWeekday(dto.getWeekday());
        jpa.setSubject(dto.getSubject());
        return jpa;
    }
}
