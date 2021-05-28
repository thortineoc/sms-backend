package com.sms.timetableservice.timetable.control;

import com.sms.api.timetable.ConflictDTO;
import com.sms.api.timetable.SimpleTimetableDTO;
import com.sms.api.timetable.TimetableDTO;
import com.sms.model.timetable.TimetableJPA;

import java.util.*;
import java.util.stream.Collectors;

public class TimetableMapper {

    private TimetableMapper() {

    }

    public static List<SimpleTimetableDTO> toDTO(TimetableDTO dto) {
        return dto.getTimetable().stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    public static TimetableJPA toJPA(SimpleTimetableDTO dto) {
        dto.getRoom();
        TimetableJPA jpa = new TimetableJPA();
        dto.getId().ifPresent(jpa::setId);
        jpa.setGroups(dto.getGroup());
        jpa.setTeacherId(dto.getTeacherId());
        jpa.setLesson(dto.getLesson());
        jpa.setRoom(dto.getRoom());
        jpa.setWeekday(dto.getWeekday());
        jpa.setSubject(dto.getSubject());
        jpa.setConflict(1L);
        return jpa;
    }

    public static SimpleTimetableDTO toDto(TimetableJPA jpa) {
        return SimpleTimetableDTO.builder()
                .id(jpa.getId())
                .group(jpa.getGroups())
                .subject(jpa.getSubject())
                .lesson(jpa.getLesson())
                .room(jpa.getRoom())
                .weekday(jpa.getWeekday())
                .teacherId(jpa.getTeacherId())
                .build();
    }

    public static ConflictDTO toConflict(SimpleTimetableDTO dto) {
        return ConflictDTO.builder()
                .from(dto)
                .conflict(Optional.empty()) //conflikt może drugi dto w paramach? nwm?
                .build();
    }

    public static List<TimetableJPA> toJPA(List<SimpleTimetableDTO> list) {
        return list.stream()
                .map(TimetableMapper::toJPA)
                .collect(Collectors.toList());
    }

    public static TimetableDTO toDTO(List<TimetableJPA> classes) {

        Map<Integer, List<SimpleTimetableDTO>> lessons = classes.stream()
                .map(TimetableMapper::toDto)
                .sorted(Comparator.comparing(SimpleTimetableDTO::getWeekday))
                .collect(Collectors.groupingBy(SimpleTimetableDTO::getWeekday, LinkedHashMap::new, Collectors.toList()));

        List<List<SimpleTimetableDTO>> simpleList = new ArrayList<>(lessons.values());

        return TimetableDTO.builder()
                .timetable(simpleList)
                .build();
    }

}
