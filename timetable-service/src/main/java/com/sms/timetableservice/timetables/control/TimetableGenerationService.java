package com.sms.timetableservice.timetables.control;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Lists;
import com.google.common.collect.Multiset;
import com.google.common.collect.Sets;
import com.sms.api.common.BadRequestException;
import com.sms.api.timetables.LessonDTO;
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.sms.timetableservice.timetables.control.TimetableGenerator.DAYS;

@Component
@Scope("request")
public class TimetableGenerationService {

    @Autowired
    ConfigResource configResource;

    @Autowired
    TimetableRepository timetableRepository;

    @Autowired
    UserManagementClient userManagementClient;

    @Autowired
    TimetableDeleteService deleteService;

    @Transactional
    public TimetableDTO generateTimetable(String group, Map<String, Map<String, Integer>> info) {
        TimetableConfigDTO config = getConfig();
        Multiset<TeacherWithSubject> teachersWithSubjects = convertToFlatList(info);

        if (teachersWithSubjects.size() > config.getLessonCount() * DAYS) {
            throw new BadRequestException("Can't add more lessons than the maximum amount of "
                    + config.getLessonCount() + " per day (that means " + config.getLessonCount() * DAYS + " in total).");
        }

        Map<String, UserDTO> teachers = getTeachersById(teachersWithSubjects);
        validateRequest(group, teachers, info);
        Map<String, Map<LessonKey, ClassJPA>> potentialConflicts = getPotentialConflicts(teachersWithSubjects);

        deleteService.deleteTimetable(group);
        Map<LessonKey, ClassJPA> generatedTimetable = new TimetableGenerator(group, config, teachersWithSubjects, potentialConflicts)
                .generate();
        List<ClassJPA> savedClasses = Lists.newArrayList(timetableRepository.saveAll(generatedTimetable.values()));

        return TimetableMapper.toDTO(savedClasses, teachers);
    }

    private void validateRequest(String group, Map<String, UserDTO> teachers, Map<String, Map<String, Integer>> info) {
        Set<String> realSubjects = userManagementClient.getSubjects();
        Set<String> realGroups = userManagementClient.getGroups();
        validateGroup(realGroups, group);
        validateSubjects(realSubjects, teachers, info);
    }

    void validateSubjects(Set<String> realSubjects, Map<String, UserDTO> realTeachers, Map<String, Map<String, Integer>> info) {
        if (!realTeachers.keySet().equals(info.keySet())) {
            throw new IllegalStateException("Some of the teachers don't exist");    // TODO: why is this 500 and not 400?
        }

        info.forEach((teacher, subjects) -> validateTeacherWithSubjects(realSubjects, realTeachers, teacher, subjects));
    }

    void validateTeacherWithSubjects(Set<String> realSubjects, Map<String, UserDTO> realTeachers, String teacher, Map<String, Integer> subjects) {
        if (!realSubjects.containsAll(subjects.keySet())) {
            throw new BadRequestException("Subjects: " + Sets.symmetricDifference(subjects.keySet(), realSubjects) + " don't exist");
        }

        Set<String> teacherSubjects = new HashSet<>(realTeachers.get(teacher).getCustomAttributes().getSubjects());
        if (!teacherSubjects.containsAll(subjects.keySet())) {
            throw new BadRequestException("Teacher: " + teacher + " does not teach subjects: " + Sets.symmetricDifference(subjects.keySet(), teacherSubjects));
        }
    }

    private void validateGroup(Set<String> realGroups, String group) {
        if (!realGroups.contains(group)) {
            throw new BadRequestException("Group " + group + " does not exist");
        }
    }

    Multiset<TeacherWithSubject> convertToFlatList(Map<String, Map<String, Integer>> info) {
        return info.entrySet().stream()
                .flatMap(entry -> entry.getValue().entrySet().stream()
                        .flatMap(subjects -> IntStream.range(0, subjects.getValue())
                                .mapToObj(i -> subjects.getKey()))
                        .map(subject -> new TeacherWithSubject(entry.getKey(), subject)))
                .collect(Collectors.toCollection(HashMultiset::create));
    }

    public Multiset<TeacherWithSubject> convertToFlatList(List<LessonDTO> lessons){
        List<TeacherWithSubject> list = lessons.stream().
               map(e -> new TeacherWithSubject(e.getTeacherId().get(), e.getSubject())).collect(Collectors.toList());
        return HashMultiset.create(list);
    }

    public Map<String, UserDTO> getTeachersById(Multiset<TeacherWithSubject> subjects) {
        Set<String> ids = subjects.stream().map(TeacherWithSubject::getTeacherId).collect(Collectors.toSet());
        return userManagementClient.getUsers(ids).stream()
                .collect(Collectors.toMap(UserDTO::getId, Function.identity()));
    }

    public Map<String, Map<LessonKey, ClassJPA>> getPotentialConflicts(Multiset<TeacherWithSubject> teachers) {
        List<String> teacherIds = teachers.stream()
                .map(TeacherWithSubject::getTeacherId)
                .distinct()
                .collect(Collectors.toList());
        return timetableRepository.findAllByTeacherIdIn(teacherIds).stream()
                .collect(Collectors.groupingBy(ClassJPA::getTeacherId,
                        Collectors.toMap(LessonKey::new, Function.identity())));
    }

    private TimetableConfigDTO getConfig() {
        return configResource.getConfigDTO()
                .orElseThrow(() -> new BadRequestException("Cannot generate timetable if the timetable configuration doesn't exist"));
    }

}
