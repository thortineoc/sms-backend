package com.sms.timetableservice.timetables.boundary;

import com.sms.api.timetables.TimetableDTO;
import com.sms.api.usermanagement.UserDTO;
import com.sms.context.AuthRole;
import com.sms.context.UserContext;
import com.sms.timetableservice.timetables.control.TimetableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/timetables")
@Scope("request")
public class TimetableResource {

    @Autowired
    UserContext userContext;

    @Autowired
    TimetableService timetableService;

    @PostMapping("/generate/{group}")
    @AuthRole(UserDTO.Role.ADMIN)
    public ResponseEntity<TimetableDTO> generateTimetable(@PathVariable("group") String group,
                                                          @RequestBody Map<String, Map<String, Integer>> info) {
        TimetableDTO timetable = timetableService.generateTimetable(group, info);
        return ResponseEntity.ok(timetable);
    }
}
