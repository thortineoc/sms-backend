package com.sms.timetableservice.timetables.control;

import com.google.common.collect.Sets;
import com.sms.timetableservice.timetables.entity.ClassJPA;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

class TimetableReadServiceTest {

    private final TimetableCommonService service = new TimetableCommonService();

    @Test
    void shouldReturnConflictsByIds() {
        // GIVEN
        List<ClassJPA> classes = Arrays.asList(getLesson(1L, "2,3,4"),
                getLesson(2L, "1,3,4"),
                getLesson(3L, "1,2,4"),
                getLesson(4L, "1,2,3"));

        // WHEN
        Set<Long> ids = service.getConflictIds(classes);

        // THEN
        Assertions.assertThat(ids).isEqualTo(Sets.newHashSet(1L, 2L, 3L, 4L));
    }

    private ClassJPA getLesson(Long id, String conflicts) {
        ClassJPA jpa = new ClassJPA();
        jpa.setConflicts(conflicts);
        jpa.setId(id);
        return jpa;
    }
}
