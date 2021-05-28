package com.sms.timetableservice.timetables.control;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.sms.api.common.BadRequestException;
import com.sms.api.common.Util;
import com.sms.api.timetables.TimetableConfigDTO;
import com.sms.api.timetables.TimetableDTO;
import com.sms.api.usermanagement.UserDTO;
import com.sms.timetableservice.clients.UserManagementClient;
import com.sms.timetableservice.config.boundary.ConfigResource;
import com.sms.timetableservice.timetables.entity.ClassJPA;
import com.sms.timetableservice.timetables.entity.LessonKey;
import com.sms.timetableservice.timetables.entity.TeacherWithSubject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.sms.timetableservice.timetables.control.TimetableGenerator.DAYS;

@Component
@Scope("request")
public class TimetableService {

    @Autowired
    ConfigResource configResource;

    @Autowired
    TimetableRepository timetableRepository;

    @Autowired
    UserManagementClient userManagementClient;

    @Transactional                                 // { "teacherId": { "subject": count } }
    public TimetableDTO generateTimetable(String group, Map<String, Map<String, Integer>> info) {
        TimetableConfigDTO config = getConfig();
        Set<TeacherWithSubject> teachersWithSubjects = convertToFlatList(info);

        if (teachersWithSubjects.size() > config.getLessonCount() * DAYS) {
            throw new BadRequestException("Can't add more lessons than the maximum amount of "
                    + config.getLessonCount() + " per day (that means " + config.getLessonCount() * DAYS + " in total).");
        }

        Map<String, UserDTO> teachers = getTeachersById(teachersWithSubjects);
        validateRequest(group, teachers, info);

        Map<String, Map<LessonKey, ClassJPA>> potentialConflicts = getPotentialConflicts(teachersWithSubjects);

        timetableRepository.deleteAllByGroup(group);
        List<ClassJPA> generatedTimetable = new TimetableGenerator(group, config, teachersWithSubjects, potentialConflicts)
                .generate();
        List<ClassJPA> savedClasses = Lists.newArrayList(timetableRepository.saveAll(generatedTimetable));

        return TimetableMapper.toDTO(savedClasses, teachers);
    }

    private void validateRequest(String group, Map<String, UserDTO> teachers, Map<String, Map<String, Integer>> info) {
        Set<String> realSubjects = userManagementClient.getSubjects();
        Set<String> realGroups = userManagementClient.getGroups();
        validateGroup(realGroups, group);
        validateSubjects(realSubjects, teachers, info);
    }

    /*TODO: unit test*/
    void validateSubjects(Set<String> realSubjects, Map<String, UserDTO> teachers, Map<String, Map<String, Integer>> info) {
        if (!teachers.keySet().equals(info.keySet())) {
            throw new IllegalStateException("Some of the teachers don't exist");
        }

        info.forEach((teacher, subjects) -> {
            if (!realSubjects.containsAll(subjects.keySet())) {
                throw new BadRequestException("Subjects: " + Sets.symmetricDifference(subjects.keySet(), realSubjects) + " don't exist");
            }

            Set<String> teacherSubjects = new HashSet<>(teachers.get(teacher).getCustomAttributes().getSubjects());
            if (!teacherSubjects.containsAll(subjects.keySet())) {
                throw new BadRequestException("Teacher: " + teacher + " does not teach subjects: " + Sets.symmetricDifference(subjects.keySet(), teacherSubjects));
            }
        });
    }

    /*TODO: unit test*/
    void validateGroup(Set<String> realGroups, String group) {
        if (!realGroups.contains(group)) {
            throw new BadRequestException("Group " + group + " does not exist");
        }
    }

    /*TODO: unit test */
    Set<TeacherWithSubject> convertToFlatList(Map<String, Map<String, Integer>> info) {
        return info.entrySet().stream()
                .flatMap(entry -> entry.getValue().entrySet().stream()
                        .flatMap(subjects -> IntStream.range(0, subjects.getValue())
                                .mapToObj(i -> subjects.getKey()))
                        .map(subject -> new TeacherWithSubject(entry.getKey(), subject)))
                .collect(Collectors.toSet());
    }

    /*TODO: unit test */
    Map<String, UserDTO> getTeachersById(Set<TeacherWithSubject> subjects) {
        Set<String> ids = Util.map(subjects, TeacherWithSubject::getTeacherId);
        return userManagementClient.getUsers(ids).stream()
                .collect(Collectors.toMap(UserDTO::getId, Function.identity()));
    }

    private Map<String, Map<LessonKey, ClassJPA>> getPotentialConflicts(Set<TeacherWithSubject> teachers) {
        List<String> teacherIds = teachers.stream().map(TeacherWithSubject::getTeacherId).collect(Collectors.toList());
        return timetableRepository.findAllByTeacherIdIn(teacherIds).stream()
                .collect(Collectors.groupingBy(ClassJPA::getTeacherId,
                        Collectors.toMap(LessonKey::new, Function.identity())));
    }

    private TimetableConfigDTO getConfig() {
        return configResource.getConfigDTO()
                .orElseThrow(() -> new BadRequestException("Cannot generate timetable if the timetable configuration doesn't exist"));
    }
}
