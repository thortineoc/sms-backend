package com.sms.timetableservice.timetable.control;

import com.sms.api.timetable.SimpleTimetableDTO;
import com.sms.api.timetable.TimetableConflictDTO;
import com.sms.api.timetable.TimetableDTO;
import com.sms.context.UserContext;
import com.sms.model.timetable.TimetableJPA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
@Scope("request")
public class TimetableService {

    @Autowired
    UserContext userContext;

    @Autowired
    TimetableRepository timetableRepository;

    public List<TimetableConflictDTO> createClass(TimetableDTO timetableDTO) {
        List<SimpleTimetableDTO> timetable= timetableDTO.getUserTimetable().stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());

        if(timetable.isEmpty()) throw new IllegalStateException("Are you kidding me?");
        return TimetableMapper.toDto(timetableRepository.saveAll(TimetableMapper.toJPA(timetable)));


    }


}
