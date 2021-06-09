package com.sms.timetableservice.timetables.control;

import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Multiset;
import com.sms.api.common.NotFoundException;
import com.sms.api.timetables.LessonDTO;
import com.sms.api.timetables.LessonsDTO;
import com.sms.api.timetables.TimetableDTO;
import com.sms.timetableservice.timetables.entity.ClassJPA;
import com.sms.timetableservice.timetables.entity.LessonKey;
import com.sms.timetableservice.timetables.entity.TeacherWithSubject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Scope("request")
public class TimetableCreateService {

    @Autowired
    TimetableRepository timetableRepository;

    @Autowired
    TimetableCommonService commonService;

    @Autowired
    TimetableGenerationService generationService;

    @Autowired
    TimetableReadService timetableReadService;

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

    @Transactional
    public TimetableDTO createClass(LessonsDTO timetableDTO) {

        List<LessonDTO> timetable = TimetableMapper.toDTO(timetableDTO);
        if (timetable.isEmpty()) throw new IllegalStateException("Request is empty");

        Multiset<TeacherWithSubject> teachersWithSubjects = generationService.convertToFlatList(timetable);
        Map<String, Map<LessonKey, ClassJPA>> conflicts = generationService.getPotentialConflicts(teachersWithSubjects);
        List<ClassJPA> listOfConflicts = conflicts.values().stream()
                .flatMap(e -> new ArrayList<>(e.values()).stream())
                .collect(Collectors.toList());

        Multimap<LessonKey, ClassJPA> multimap= Multimaps.index(listOfConflicts, LessonKey::new);
        multimap.isEmpty();
        List<ClassJPA> jpaList = TimetableMapper.toJPA(timetable);
        List<ClassJPA> saved = saveLessons(jpaList, listOfConflicts);
        saveConflicted(saved, listOfConflicts);

        return timetableReadService.getTimetableForGroup(timetable.get(0).getGroup());
    }

    private void saveConflicted(List<ClassJPA> savedList, List<ClassJPA> listOfConflicts) {
        for (ClassJPA jpa : savedList) {
            for (ClassJPA conflict : listOfConflicts) {
                if (new LessonKey(jpa).equals(new LessonKey(conflict))) {
                    commonService.addConflictId(conflict, jpa.getId());
                }
            }
        }
            timetableRepository.saveAll(listOfConflicts); //we observed an increase of up to 60% on the saveAll() method.
    }

    private List<ClassJPA> saveLessons(List<ClassJPA> jpaList, List<ClassJPA> listOfConflicts) {
        for (ClassJPA jpa : jpaList) {
            for (ClassJPA conflict : listOfConflicts) {
                if (new LessonKey(jpa).equals(new LessonKey(conflict))) {
                    jpa.setConflicts(conflict.getId().toString());
                }
            }
        }
            Iterable<ClassJPA> saved = timetableRepository.saveAll(jpaList); //we observed an increase of up to 60% on the saveAll() method.
            return Lists.newArrayList(saved);
        }

}
