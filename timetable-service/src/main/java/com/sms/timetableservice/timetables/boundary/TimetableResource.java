package com.sms.timetableservice.timetables.boundary;

import com.sms.api.timetables.LessonDTO;
import com.sms.api.timetables.LessonsDTO;
import com.sms.api.timetables.TeacherInfoDTO;
import com.sms.api.timetables.TimetableDTO;
import com.sms.api.usermanagement.UserDTO;
import com.sms.context.AuthRole;
import com.sms.context.UserContext;
import com.sms.timetableservice.clients.UserManagementClient;
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
    private static final String RELATED_USER = "relatedUser";

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

    @Autowired
    UserManagementClient userManagementClient;

    @GetMapping("/{group}")
    @AuthRole(UserDTO.Role.ADMIN)
    public ResponseEntity<TimetableDTO> getTimetableForGroup(@PathVariable("group") String group) {
        TimetableDTO timetable = timetableReadService.getTimetableForGroup(group);
        return ResponseEntity.ok(timetable);
    }

    @GetMapping("/student")
    @AuthRole({UserDTO.Role.PARENT, UserDTO.Role.STUDENT})
    public ResponseEntity<TimetableDTO> getTimetableForStudent() {
        TimetableDTO timetable = timetableReadService.getTimetableForGroup(getGroup());
        return ResponseEntity.ok(timetable);
    }

    private String getGroup() {
        switch (userContext.getSmsRole()) {
            case STUDENT: return (String) userContext.getCustomAttributes().get(GROUP);
            case PARENT:
                String relatedUserId = (String) userContext.getCustomAttributes().get(RELATED_USER);
                if (relatedUserId == null) {
                    throw new IllegalStateException("User doesn't have a related user.");
                }
                UserDTO relatedUser = userManagementClient.getUser(relatedUserId)
                        .orElseThrow(() -> new IllegalStateException("User does not have a related user."));
                return relatedUser.getCustomAttributes().getGroup()
                        .orElseThrow(() -> new IllegalStateException("Related user does not have a group assigned."));
            default: throw new IllegalStateException("You shouldn't be here!!!");
        }
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

    @DeleteMapping("/teacher/{teacherId}")
    @AuthRole(UserDTO.Role.ADMIN)
    public ResponseEntity<Object> deleteLessonsByTeacherId(@PathVariable("teacherId") String teacherId) {
        timetableDeleteService.deleteLessonsByTeacherId(teacherId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping()
    @AuthRole(UserDTO.Role.ADMIN)
    public ResponseEntity<TimetableDTO> createLessons(@RequestBody LessonsDTO lessons){
        List<LessonDTO> les = lessons.getLessons();
        TimetableDTO timetable = timetableCreateService.createLessons(lessons);
        return ResponseEntity.ok(timetable);
    }
}
