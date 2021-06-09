package com.sms.timetableservice.timetables.entity;

import com.google.common.base.Strings;

import java.util.*;
import java.util.stream.Collectors;

public class Lesson {

    private final Long id;
    private final String group;
    private final String subject;
    private final String teacherId;
    private final String room;
    private final LessonKey key;
    private final Set<Long> conflicts;

    public Lesson(ClassJPA classJpa) {
        this.id = classJpa.getId();
        this.group = classJpa.getGroup();
        this.subject = classJpa.getSubject();
        this.teacherId = classJpa.getTeacherId();
        this.room = classJpa.getRoom();
        this.key = new LessonKey(classJpa);
        this.conflicts = getConflictIds(classJpa);
    }

    public ClassJPA toJPA() {
        ClassJPA jpa = new ClassJPA();
        jpa.setConflicts(getIdsAsString(conflicts));
        jpa.setLesson(key.getLesson());
        jpa.setWeekday(key.getWeekday());
        jpa.setGroup(group);
        jpa.setRoom(room);
        jpa.setId(id);
        jpa.setTeacherId(teacherId);
        jpa.setSubject(subject);
        return jpa;
    }

    public Long getId() {
        return id;
    }

    public String getGroup() {
        return group;
    }

    public String getSubject() {
        return subject;
    }

    public String getTeacherId() {
        return teacherId;
    }

    public LessonKey getKey() {
        return key;
    }

    public Set<Long> getConflicts() {
        return conflicts;
    }

    public String getRoom() {
        return room;
    }

    private String getIdsAsString(Set<Long> ids) {
        String result = ids.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        return "".equals(result)
                ? null
                : result;
    }

    private Set<Long> getConflictIds(ClassJPA jpa) {
        String conflicts = jpa.getConflicts();
        if (!Strings.isNullOrEmpty(conflicts)) {
            return Arrays.stream(conflicts.split(",")).map(Long::valueOf).collect(Collectors.toSet());
        } else {
            return Collections.emptySet();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Lesson lesson = (Lesson) o;
        return Objects.equals(id, lesson.id) && Objects.equals(group, lesson.group)
                && Objects.equals(subject, lesson.subject) && Objects.equals(teacherId, lesson.teacherId)
                && Objects.equals(room, lesson.room) && Objects.equals(key, lesson.key)
                && Objects.equals(conflicts, lesson.conflicts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, group, subject, teacherId, room, key, conflicts);
    }
}
