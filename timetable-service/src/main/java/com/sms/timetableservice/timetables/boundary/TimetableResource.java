package com.sms.timetableservice.timetables.boundary;

import com.sms.api.timetables.TimetableDTO;
import com.sms.api.usermanagement.UserDTO;
import com.sms.context.AuthRole;
import com.sms.context.UserContext;
import com.sms.timetableservice.timetables.control.TimetableReadService;
import com.sms.timetableservice.timetables.control.TimetableGenerationService;
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
    TimetableGenerationService timetableGenerationService;

    @Autowired
    TimetableReadService timetableReadService;

    @GetMapping("/{group}")
    @AuthRole({UserDTO.Role.ADMIN, UserDTO.Role.STUDENT, UserDTO.Role.PARENT})
    public ResponseEntity<TimetableDTO> getTimetableForGroup(@PathVariable("group") String group) {
        TimetableDTO timetable = timetableReadService.getTimetableForGroup(group);
        return ResponseEntity.ok(timetable);
    }

    @GetMapping
    @AuthRole(UserDTO.Role.TEACHER)
    public ResponseEntity<TimetableDTO> getTimetableForTeacher() {
        TimetableDTO timetable = timetableReadService.getTimetableForTeacher();
        return ResponseEntity.ok(timetable);
    }

    @PostMapping("/generate/{group}")
    @AuthRole(UserDTO.Role.ADMIN)
    public ResponseEntity<TimetableDTO> generateTimetable(@PathVariable("group") String group,
                                                          @RequestBody Map<String, Map<String, Integer>> info) {
        TimetableDTO timetable = timetableGenerationService.generateTimetable(group, info);
        return ResponseEntity.ok(timetable);
    }
}
