package com.sms.timetableservice.timetable.control;

import com.sms.api.timetable.SimpleTimetableDTO;
import com.sms.api.timetable.TimetableConflictDTO;
import com.sms.model.timetable.TimetableJPA;
import org.apache.http.HttpMessage;
import org.springframework.http.HttpStatus;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class TimetableMapper {

    private TimetableMapper() {

    }

    public static TimetableJPA toJPA(SimpleTimetableDTO dto) {
        TimetableJPA jpa = new TimetableJPA();
        dto.getId().ifPresent(jpa::setId);
        jpa.setGroup(dto.getGroup());
        jpa.setTeacherId(dto.getTeacherId());
        jpa.setBegindate(Timestamp.valueOf(dto.getBegindate()));
        jpa.setEnddate(Timestamp.valueOf(dto.getEnddate()));
        jpa.setLesson(dto.getLesson());
        jpa.setRoom(dto.getRoom());
        jpa.setWeekday(dto.getWeekday());
        return jpa;
    }

    public static SimpleTimetableDTO toDto(TimetableJPA jpa) {
        return SimpleTimetableDTO.builder()
                .id(jpa.getId())
                .group(jpa.getGroup())
                .subject(jpa.getSubject())
                .lesson(jpa.getLesson())
                .room(jpa.getRoom())
                .weekday(jpa.getWeekday())
                .begindate(jpa.getBegindate().toLocalDateTime())
                .enddate(jpa.getEnddate().toLocalDateTime())
                .build();
    }

    public static TimetableConflictDTO conflict(SimpleTimetableDTO dto, String info){
        return TimetableConflictDTO.builder()
                .from(dto)
                .addInfo(info)
                .build();
    }

    public static List<TimetableJPA> toJPA(List<SimpleTimetableDTO> list){
        List<TimetableJPA> jpa = new ArrayList<>();
        for(SimpleTimetableDTO dto : list){
            jpa.add(TimetableMapper.toJPA(dto));
        }
        return jpa;
    }

    public static List<SimpleTimetableDTO> toDTO(Iterable<TimetableJPA> jpa){
        List<SimpleTimetableDTO> dtoList = new ArrayList<>();
        for(TimetableJPA item : jpa){
            dtoList.add(TimetableMapper.toDto(item));
        }
        dtoList.stream().map()
    }
}
