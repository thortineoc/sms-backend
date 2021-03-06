package com.sms.timetableservice.timetables.control;


import com.google.common.collect.Sets;
import com.sms.api.timetables.LessonDTO;
import com.sms.api.timetables.TeacherInfoDTO;
import com.sms.api.timetables.TimetableDTO;
import com.sms.api.usermanagement.UserDTO;
import com.sms.context.UserContext;
import com.sms.timetableservice.clients.UserManagementClient;
import com.sms.timetableservice.timetables.entity.ClassJPA;
import com.sms.timetableservice.timetables.entity.Lesson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Scope("request")
public class TimetableReadService {

    @Autowired
    TimetableRepository timetableRepository;

    @Autowired
    UserManagementClient userManagementClient;

    @Autowired
    UserContext userContext;

    @Autowired
    TimetableCommonService commonService;

    public List<TeacherInfoDTO> getTeacherInfo() {
        Map<String, UserDTO> teachers = getTeachersByIds();
        Map<String, Map<String, Long>> lessonsByTeacherId = timetableRepository.findAllByTeacherIdIn(teachers.keySet()).stream()
                .map(Lesson::new)
                .collect(Collectors.groupingBy(Lesson::getTeacherId,
                        Collectors.groupingBy(Lesson::getSubject, Collectors.counting())));
        return teachers.keySet().stream()
                .map(id -> TeacherInfoDTO.builder()
                        .teacher(teachers.get(id))
                        .lessonCount(lessonsByTeacherId.getOrDefault(id, Collections.emptyMap()))
                        .build())
                .collect(Collectors.toList());
    }

    public List<LessonDTO> getConflictsGlobal() {
        return TimetableMapper.toDTOs(timetableRepository.findAllConflicted());
    }

    public TimetableDTO getTimetableForGroup(String group) {
        List<Lesson> lessons = TimetableMapper.toLessons(timetableRepository.findAllByGroup(group));

        // teachers
        Set<String> teacherIds = lessons.stream().map(Lesson::getTeacherId).collect(Collectors.toSet());
        Map<String, UserDTO> teachers = getTeachersByIds(teacherIds);
        validateAllTeachersExist(teacherIds, teachers.keySet());

        // conflicts
        Set<Long> conflictIds = commonService.getAllConflicts(lessons);
        Map<Long, ClassJPA> conflicts = getConflictsByIds(conflictIds);

        return TimetableMapper.toDTO(lessons, teachers, conflicts);
    }

    public TimetableDTO getTimetableForTeacher() {
        String teacherId = userContext.getUserId();
        UserDTO currentUser = userManagementClient.getUser(teacherId)
                .orElseThrow(() -> new IllegalStateException("Teacher with id: " + teacherId + " doesn't exist"));

        List<Lesson> lessons = TimetableMapper.toLessons(timetableRepository.findAllByTeacherId(teacherId));
        Set<Long> conflictIds = commonService.getAllConflicts(lessons);
        Map<Long, ClassJPA> conflicts = getConflictsByIds(conflictIds);

        return TimetableMapper.toDTO(lessons, Collections.singletonMap(teacherId, currentUser), conflicts);
    }

    private Map<Long, ClassJPA> getConflictsByIds(Set<Long> ids) {
        return timetableRepository.findAllByIdIn(ids).stream()
                .collect(Collectors.toMap(ClassJPA::getId, Function.identity()));
    }

    private void validateAllTeachersExist(Set<String> expectedTeacherIds, Set<String> teacherIds) {
        if (!expectedTeacherIds.equals(teacherIds)) {
            throw new IllegalStateException("Teachers: " + Sets.difference(expectedTeacherIds, teacherIds) + " don't exist");
        }
    }

    private Map<String, UserDTO> getTeachersByIds() {
        return userManagementClient.getAllTeachers().stream()
                .collect(Collectors.toMap(UserDTO::getId, Function.identity()));
    }

    private Map<String, UserDTO> getTeachersByIds(Set<String> teacherIds) {
        return userManagementClient.getUsers(teacherIds).stream()
                .collect(Collectors.toMap(UserDTO::getId, Function.identity()));
    }
}
