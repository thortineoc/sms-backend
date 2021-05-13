package com.sms.model.homework;

import javax.persistence.*;

@Entity
@Table(name = "homeworksfiles")
public class HomeworkFileJPA extends FileJPA {

    @ManyToOne(fetch = FetchType.LAZY)
    private HomeworkJPA homework;
}
