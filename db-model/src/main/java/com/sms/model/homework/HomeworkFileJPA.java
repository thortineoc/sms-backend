package com.sms.model.homework;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "homeworksfiles")
public class HomeworkFileJPA extends FileJPA {

    @ManyToOne(fetch = FetchType.LAZY)
    private HomeworkJPA homework;
}
