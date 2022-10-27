package com.zancheema.classroom.answer;

import com.zancheema.classroom.question.Question;
import com.zancheema.classroom.solution.Solution;

import javax.persistence.*;

@Entity
public class Answer {
    @Id
    @GeneratedValue
    private long id;

    @ManyToOne(optional = false)
private Question question;

    @ManyToOne(optional = false)
private Solution solution;

    @Column(nullable = false)
    private String value;

    public Answer() {
    }

    public Answer(Question question, Solution solution, String value) {
        this.question = question;
        this.solution = solution;
        this.value = value;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public Solution getSolution() {
        return solution;
    }

    public void setSolution(Solution solution) {
        this.solution = solution;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
