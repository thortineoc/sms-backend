package com.sms.timetableservice.timetable.control;

import com.sms.api.timetable.SimpleTimetableDTO;
import com.sms.api.timetable.TimetableDTO;
import com.sms.context.UserContext;
import com.sms.model.timetable.TimetableJPA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

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

    public TimetableDTO createClass(TimetableDTO timetableDTO) {

        List<SimpleTimetableDTO> timetable = TimetableMapper.toDTO(timetableDTO);
        if (timetable.isEmpty()) throw new IllegalStateException("Are you kidding me?");

        List<TimetableJPA> jpaList = TimetableMapper.toJPA(timetable);
        Iterable<TimetableJPA> saved;
        try {
            saved = timetableRepository.saveAll(jpaList); //we observed an increase of up to 60% on the saveAll() method.
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        return TimetableMapper.toDTO(StreamSupport.stream(saved.spliterator(), false).collect(Collectors.toList()));
    }


}
