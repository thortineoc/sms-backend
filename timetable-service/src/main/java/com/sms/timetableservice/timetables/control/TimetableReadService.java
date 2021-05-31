package com.sms.timetableservice.timetables.control;


import com.google.common.collect.Sets;
import com.sms.api.timetables.TimetableDTO;
import com.sms.api.usermanagement.UserDTO;
import com.sms.context.UserContext;
import com.sms.timetableservice.clients.UserManagementClient;
import com.sms.timetableservice.timetables.entity.ClassJPA;
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

    public TimetableDTO getTimetableForGroup(String group) {
        List<ClassJPA> classes = timetableRepository.findAllByGroup(group);
        Map<Long, ClassJPA> conflicts = getConflictsByClassId(classes);
        Set<String> teacherIds = classes.stream().map(ClassJPA::getTeacherId).collect(Collectors.toSet());
        Map<String, UserDTO> teachers = getTeachersByIds(teacherIds);

        validateAllTeachersExist(teacherIds, teachers.keySet());
        Set<Long> conflictIds = commonService.getConflictIds(classes);
        return TimetableMapper.toDTO(classes, conflictIds, teachers, conflicts);
    }

    public TimetableDTO getTimetableForTeacher() {
        String teacherId = userContext.getUserId();
        UserDTO currentUser = userManagementClient.getUser(teacherId)
                .orElseThrow(() -> new IllegalStateException("Teacher with id: " + teacherId + " doesn't exist"));

        List<ClassJPA> classes = timetableRepository.findAllByTeacherId(teacherId);
        Map<Long, ClassJPA> conflicts = getConflictsByClassId(classes);
        Set<Long> conflictIds = commonService.getConflictIds(classes);
        return TimetableMapper.toDTO(classes, conflictIds, Collections.singletonMap(teacherId, currentUser), conflicts);
    }

    private Map<Long, ClassJPA> getConflictsByClassId(List<ClassJPA> classes) {
        Set<Long> conflictIds = commonService.getConflictIds(classes);
        return timetableRepository.findAllByIdIn(conflictIds).stream()
                .collect(Collectors.toMap(ClassJPA::getId, Function.identity()));
    }

    private void validateAllTeachersExist(Set<String> expectedTeacherIds, Set<String> teacherIds) {
        if (!expectedTeacherIds.equals(teacherIds)) {
            throw new IllegalStateException("Teachers: " + Sets.difference(expectedTeacherIds, teacherIds) + " don't exist");
        }
    }

    private Map<String, UserDTO> getTeachersByIds(Set<String> teacherIds) {
        return userManagementClient.getUsers(teacherIds).stream()
                .collect(Collectors.toMap(UserDTO::getId, Function.identity()));
    }
}
