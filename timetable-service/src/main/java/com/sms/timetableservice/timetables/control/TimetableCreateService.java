package com.sms.timetableservice.timetables.control;

import com.google.common.collect.Multiset;
import com.sms.api.common.NotFoundException;
import com.sms.api.timetables.LessonDTO;
import com.sms.api.timetables.LessonsDTO;
import com.sms.api.timetables.TimetableDTO;
import com.sms.api.usermanagement.UserDTO;
import com.sms.timetableservice.timetables.entity.ClassJPA;
import com.sms.timetableservice.timetables.entity.LessonKey;
import com.sms.timetableservice.timetables.entity.TeacherWithSubject;
import org.aopalliance.reflect.Class;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
@Scope("request")
public class TimetableCreateService {

    @Autowired
    TimetableRepository timetableRepository;

    @Autowired
    TimetableCommonService commonService;

    @Autowired
    TimetableGenerationService generationService;

    @Transactional
    public void moveLesson(Long id, Integer day, Integer lesson) {
        ClassJPA jpa = timetableRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Lesson with id: " + id + " doesn't exist."));

        if (jpa.getWeekday().equals(day) && jpa.getLesson().equals(lesson)) {
            return;
        }
        Set<ClassJPA> classesToUpdate = new HashSet<>(commonService.removeFromConflicts(jpa));
        if (1 != timetableRepository.moveClass(id, day, lesson)) {
            throw new IllegalStateException("Couldn't move lesson with id: "
                    + id + " to day: " + day + " and lesson: " + lesson);
        }
        classesToUpdate.addAll(commonService.addToConflicts(jpa));
        timetableRepository.saveAll(classesToUpdate);
        timetableRepository.updateConflicts(jpa.getConflicts(), jpa.getId());
    }


    public TimetableDTO createClass(LessonsDTO timetableDTO) {

        List<LessonDTO> timetable = TimetableMapper.toDTO(timetableDTO);
        if (timetable.isEmpty()) throw new IllegalStateException("Request is empty");

        Multiset<TeacherWithSubject> teachersWithSubjects = generationService.convertToFlatList(timetable);
        Map<String, Map<LessonKey, ClassJPA>> conflicts = generationService.getPotentialConflicts(teachersWithSubjects);
        List<ClassJPA> listOfConflicts = conflicts.values().stream().map(e -> new ArrayList<>(e.values())).collect(Collectors.toList()).stream().flatMap(List::stream).collect(Collectors.toList());
        Map<String, UserDTO> teachers = generationService.getTeachersById(teachersWithSubjects);
        List<ClassJPA> jpaList = TimetableMapper.toJPA(timetable);

        List<ClassJPA> saved = saveLessons(jpaList, listOfConflicts);
        Set<Long> conflictedIDs = saveConflicted(saved, listOfConflicts);
        Map<Long, ClassJPA> classMap =listOfConflicts.stream().collect(Collectors.toMap(ClassJPA::getId, Function.identity()));

        return TimetableMapper.toDTO(saved, conflictedIDs, teachers, classMap);
    }

    private Set<Long> saveConflicted(List<ClassJPA> savedList, List<ClassJPA> listOfConflicts) {
        Set<Long> conflictsID = new HashSet<>();
        for (ClassJPA jpa : savedList) {
            for (ClassJPA conflict : listOfConflicts) {
                if (new LessonKey(jpa).equals(new LessonKey(conflict))) {
                    conflict.setConflicts(jpa.getId().toString());
                    conflictsID.add(jpa.getId());
                }
            }
        }
        try {
            timetableRepository.saveAll(listOfConflicts); //we observed an increase of up to 60% on the saveAll() method.
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        return conflictsID;
    }

    private List<ClassJPA> saveLessons(List<ClassJPA> jpaList, List<ClassJPA> listOfConflicts) {
        for (ClassJPA jpa : jpaList) {
            for (ClassJPA conflict : listOfConflicts) {
                if (new LessonKey(jpa).equals(new LessonKey(conflict))) {
                    jpa.setConflicts(conflict.getId().toString());
                }
            }
        }
        Iterable<ClassJPA> saved;
        try {
            saved = timetableRepository.saveAll(jpaList); //we observed an increase of up to 60% on the saveAll() method.
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        return StreamSupport.stream(saved.spliterator(), false).collect(Collectors.toList());
    }
}
