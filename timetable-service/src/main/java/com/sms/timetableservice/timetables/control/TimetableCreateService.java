package com.sms.timetableservice.timetables.control;

import com.google.common.collect.Lists;
import com.sms.api.common.NotFoundException;
import com.sms.api.timetables.LessonDTO;
import com.sms.api.timetables.LessonsDTO;
import com.sms.api.timetables.TimetableDTO;
import com.sms.timetableservice.timetables.entity.ClassJPA;
import com.sms.timetableservice.timetables.entity.Lesson;
import com.sms.timetableservice.timetables.entity.LessonKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Scope("request")
public class TimetableCreateService {

    @Autowired
    TimetableRepository timetableRepository;

    @Autowired
    TimetableCommonService commonService;

    @Autowired
    TimetableDeleteService deleteService;

    @Autowired
    TimetableReadService readService;

    @Transactional
    public void moveLesson(Long id, Integer day, Integer lessonNumber) {
        Lesson lesson = getLesson(id);

        if (lesson.getKey().equals(new LessonKey(day, lessonNumber))) {
            return;
        }
        Map<Long, Lesson> conflictsBefore = TimetableMapper.toLessonsById(timetableRepository.findAllByIdIn(lesson.getConflicts()));
        commonService.removeFromConflicts(conflictsBefore.values(), lesson.getId());

        if (1 != timetableRepository.moveClass(id, day, lessonNumber)) {
            throw new IllegalStateException("Couldn't move lesson with id: "
                    + id + " to day: " + day + " and lesson: " + lesson);
        }
        Map<Long, Lesson> conflictsAfter = calculateConflicts(id);

        conflictsAfter.putAll(conflictsBefore);
        List<ClassJPA> updatedConflicts = conflictsAfter.values().stream().map(Lesson::toJPA).collect(Collectors.toList());
        timetableRepository.saveAll(updatedConflicts);
    }

    private Map<Long, Lesson> calculateConflicts(Long id) {
        Lesson lesson = getLesson(id);
        Map<Long, Lesson> conflictsAfter = TimetableMapper.toLessonsById(
                timetableRepository.findConflicts(lesson.getKey().getWeekday(), lesson.getKey().getLesson(), lesson.getTeacherId()));
        conflictsAfter.values().removeIf(v -> v.getId().equals(id));
        commonService.addToConflicts(conflictsAfter.values(), lesson.getId());
        lesson.getConflicts().addAll(conflictsAfter.keySet());
        timetableRepository.save(lesson.toJPA());
        return conflictsAfter;
    }

    private Lesson getLesson(Long id) {
        return new Lesson(timetableRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Lesson with id: " + id + " doesn't exist.")));
    }

    @Transactional
    public TimetableDTO createLessons(LessonsDTO lessonsDTO){
        List<LessonDTO> lessonDTOS = lessonsDTO.getLessons();
        lessonDTOS.forEach(e -> e.getId().ifPresent(deleteService::deleteLesson));
        List<ClassJPA> savedLessons = Lists.newArrayList(timetableRepository.saveAll(TimetableMapper.toJPA(lessonDTOS)));
        for (ClassJPA jpa : savedLessons) {
            Map<Long, Lesson> conflictsAfter = calculateConflicts(jpa.getId());
            List<ClassJPA> updatedConflicts = conflictsAfter.values().stream().map(Lesson::toJPA).collect(Collectors.toList());
            timetableRepository.saveAll(updatedConflicts);
        }
       return readService.getTimetableForGroup(lessonDTOS.get(0).getGroup());
    }
}
