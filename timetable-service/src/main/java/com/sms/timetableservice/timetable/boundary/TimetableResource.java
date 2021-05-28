package com.sms.timetableservice.timetable.boundary;


import com.sms.api.timetable.SimpleTimetableDTO;
import com.sms.api.timetable.TimetableDTO;
import com.sms.api.usermanagement.UserDTO;
import com.sms.context.AuthRole;
import com.sms.context.UserContext;
import com.sms.timetableservice.timetable.control.TimetableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/timetable")
@Scope("request")
public class TimetableResource {

    @Autowired
    TimetableService timetableService;

    @Autowired
    UserContext userContext;

    @AuthRole(UserDTO.Role.ADMIN)
    @PutMapping()
    public ResponseEntity<TimetableDTO> createClass(@RequestBody TimetableDTO dto){
        TimetableDTO timetableDTO = timetableService.createClass(dto);
        return ResponseEntity.ok(timetableDTO);
    }

}
