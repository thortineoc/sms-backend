package com.sms.timetableservice.timetables.boundary;

import com.sms.api.timetables.LessonDTO;
import com.sms.api.timetables.TeacherInfoDTO;
import com.sms.api.timetables.TimetableDTO;
import com.sms.api.usermanagement.UserDTO;
import com.sms.context.AuthRole;
import com.sms.context.UserContext;
import com.sms.timetableservice.timetables.control.TimetableCreateService;
import com.sms.timetableservice.timetables.control.TimetableDeleteService;
import com.sms.timetableservice.timetables.control.TimetableReadService;
import com.sms.timetableservice.timetables.control.TimetableGenerationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/timetables")
@Scope("request")
public class TimetableResource {

    private static final String GROUP = "group";

    @Autowired
    UserContext userContext;

    @Autowired
    TimetableGenerationService timetableGenerationService;

    @Autowired
    TimetableReadService timetableReadService;

    @Autowired
    TimetableDeleteService timetableDeleteService;

    @Autowired
    TimetableCreateService timetableCreateService;

    @GetMapping("/{group}")
    @AuthRole(UserDTO.Role.ADMIN)
    public ResponseEntity<TimetableDTO> getTimetableForGroup(@PathVariable("group") String group) {
        TimetableDTO timetable = timetableReadService.getTimetableForGroup(group);
        return ResponseEntity.ok(timetable);
    }

    @GetMapping("/student")
    @AuthRole({UserDTO.Role.PARENT, UserDTO.Role.STUDENT})
    public ResponseEntity<TimetableDTO> getTimetableForStudent() {
        String group = (String) userContext.getCustomAttributes().get(GROUP);
        if (group == null) {
            throw new IllegalStateException("User: " + userContext.getUserName() + " doesn't have a group assigned.");
        }
        TimetableDTO timetable = timetableReadService.getTimetableForGroup(group);
        return ResponseEntity.ok(timetable);
    }

    @GetMapping("/teacher")
    @AuthRole(UserDTO.Role.TEACHER)
    public ResponseEntity<TimetableDTO> getTimetableForTeacher() {
        TimetableDTO timetable = timetableReadService.getTimetableForTeacher();
        return ResponseEntity.ok(timetable);
    }

    @GetMapping("/teacher/info")
    @AuthRole(UserDTO.Role.ADMIN)
    public ResponseEntity<List<TeacherInfoDTO>> getTeacherInfo() {
        List<TeacherInfoDTO> teacherInfo = timetableReadService.getTeacherInfo();
        return ResponseEntity.ok(teacherInfo);
    }

    @GetMapping("/conflicts")
    @AuthRole(UserDTO.Role.ADMIN)
    public ResponseEntity<List<LessonDTO>> getConflictInfo() {
        List<LessonDTO> conflicts = timetableReadService.getConflictsGlobal();
        if (conflicts.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(conflicts);
        }
    }

    @PostMapping("/generate/{group}")
    @AuthRole(UserDTO.Role.ADMIN)
    public ResponseEntity<TimetableDTO> generateTimetable(@PathVariable("group") String group,
                                                          @RequestBody Map<String, Map<String, Integer>> info) {
        TimetableDTO timetable = timetableGenerationService.generateTimetable(group, info);
        return ResponseEntity.ok(timetable);
    }

    @PutMapping("/move/{id}/to/{day}/{lesson}")
    public ResponseEntity<Object> moveLesson(@PathVariable("id") Long id,
                                             @PathVariable("day") Integer day,
                                             @PathVariable("lesson") Integer lesson) {
        timetableCreateService.moveLesson(id, day, lesson);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/group/{group}")
    @AuthRole(UserDTO.Role.ADMIN)
    public ResponseEntity<Object> deleteTimetable(@PathVariable("group") String group) {
        timetableDeleteService.deleteTimetable(group);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/id/{id}")
    @AuthRole(UserDTO.Role.ADMIN)
    public ResponseEntity<Object> deleteLesson(@PathVariable("id") Long id) {
        timetableDeleteService.deleteLesson(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/subject/{subject}")
    @AuthRole(UserDTO.Role.ADMIN)
    public ResponseEntity<Object> deleteClassesBySubject(@PathVariable("subject") String subject) {
        timetableDeleteService.deleteClassesBySubject(subject);
        return ResponseEntity.noContent().build();
    }
}
