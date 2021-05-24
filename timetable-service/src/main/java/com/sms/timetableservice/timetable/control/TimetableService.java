package com.sms.timetableservice.timetable.control;

import com.sms.api.timetable.SimpleTimetableDTO;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("request")
public class TimetableService {

    public SimpleTimetableDTO createClass(SimpleTimetableDTO timetableDTO){
        return timetableDTO;
    }


}
