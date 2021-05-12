package com.sms.model.homework;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "answersfiles")
public class AnswerFileJPA extends FileJPA {

    @ManyToOne(fetch = FetchType.LAZY)
    private AnswerJPA answer;
}
