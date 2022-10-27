package com.zancheema.classroom.solution;

import com.zancheema.classroom.quiz.Quiz;
import com.zancheema.classroom.user.User;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Solution {
    @Id
    @GeneratedValue
    private long id;

    @ManyToOne(optional = false)
    private Quiz quiz;

    @ManyToOne(optional = false)
    private User student;

    public Solution() {
    }

    public Solution(Quiz quiz, User student) {
        this.quiz = quiz;
        this.student = student;
    }

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

    public User getStudent() {
        return student;
    }

    public void setStudent(User student) {
        this.student = student;
    }
}
