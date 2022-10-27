package com.zancheema.classroom.question;

import com.zancheema.classroom.quiz.Quiz;

import javax.persistence.*;

@Entity
public class Question {
    @Id
    @GeneratedValue
    private long id;

    @ManyToOne(optional = false)
    private Quiz quiz;

    @Column(nullable = false)
    private String statement;

    private int order;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Quiz getQuiz() {
        return quiz;
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }

    public String getStatement() {
        return statement;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
