package com.sms.model.homework;

import javax.persistence.*;

@Entity
@Table(name = "answersfiles")
public class AnswerFileJPA extends FileJPA {

    @ManyToOne(fetch = FetchType.LAZY)
    private AnswerJPA answer;
}
