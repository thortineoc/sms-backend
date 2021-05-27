package com.sms.timetableservice.timetables.control;

import com.google.common.collect.*;
import com.sms.api.common.BadRequestException;
import com.sms.api.common.Util;
import com.sms.api.timetables.LessonDTO;
import com.sms.api.timetables.SubjectInfoDTO;
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

    @Transactional                                      // { "teacherId": { "subject": count } }
    public TimetableDTO generateTimetable(String group, Map<String, Map<String, Integer>> info) {
        TimetableConfigDTO config = getConfig();
        List<TeacherWithSubject> teachersWithSubjects = convertToFlatList(info);

        if (teachersWithSubjects.size() > config.getLessonCount()) {
            throw new BadRequestException("Can't add more lessons than the maximum amount of "
                    + config.getLessonCount() + " per day (that means " + config.getLessonCount() * DAYS + ").");
        }

        Map<String, UserDTO> teachers = getTeachersById(teachersWithSubjects);
        Set<String> missingTeachers = Sets.difference(teachers.keySet(), info.keySet());
        if (!missingTeachers.isEmpty()) {
            throw new IllegalStateException("Couldn't find teachers: " + missingTeachers);
        }

        Map<String, Map<LessonKey, ClassJPA>> potentialConflicts = getPotentialConflicts(teachersWithSubjects);
        TimetableGenerator generator = new TimetableGenerator(group, config, teachersWithSubjects, potentialConflicts);

        List<ClassJPA> generatedTimetable = generator.generate();

//        timetableRepository.deleteAllByGroup(group);
//        List<ClassJPA> savedClasses = Lists.newArrayList(timetableRepository.saveAll(generatedTimetable.values()));

        return TimetableMapper.toDTO(generatedTimetable, teachers);
    }

    /*TODO: unit test */
    List<TeacherWithSubject> convertToFlatList(Map<String, Map<String, Integer>> info) {
        List<TeacherWithSubject> list = info.entrySet().stream()
                .flatMap(entry -> entry.getValue().entrySet().stream()
                        .flatMap(subjects -> IntStream.range(0, subjects.getValue())
                                .mapToObj(i -> subjects.getKey()))
                        .map(subject -> new TeacherWithSubject(entry.getKey(), subject)))
                .collect(Collectors.toList());
        Collections.shuffle(list);
        return list;
    }

    private Map<String, UserDTO> getTeachersById(List<TeacherWithSubject> subjects) {
        List<String> ids = Util.map(subjects, TeacherWithSubject::getTeacherId);
        return userManagementClient.getUsers(ids).stream()
                .collect(Collectors.toMap(UserDTO::getId, Function.identity()));
    }

    private Map<String, Map<LessonKey, ClassJPA>> getPotentialConflicts(List<TeacherWithSubject> teachers) {
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
