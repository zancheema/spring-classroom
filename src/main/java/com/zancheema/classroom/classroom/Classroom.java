package com.zancheema.classroom.classroom;

import com.zancheema.classroom.quiz.Quiz;
import com.zancheema.classroom.user.User;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Classroom {
    @Id
    @GeneratedValue
    private long id;

    @ManyToOne(optional = false)
    private User teacher;

    @ManyToMany
    private Set<User> students = new HashSet<>();

    @OneToMany
    private Set<Quiz> quizzes = new HashSet<>();

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String subject;

    public Classroom() {
    }

    public Classroom(long id, User teacher, Set<User> students, String title, String subject) {
        this.id = id;
        this.teacher = teacher;
        this.students = students;
        this.title = title;
        this.subject = subject;
    }

    public Classroom(long id, User teacher, Set<User> students, String title, String subject, Set<Quiz> quizzes) {
        this(id, teacher, students, title, subject);
        this.quizzes = quizzes;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public User getTeacher() {
        return teacher;
    }

    public void setTeacher(User teacher) {
        this.teacher = teacher;
    }

    public Set<User> getStudents() {
        return students;
    }

    public void setStudents(Set<User> students) {
        this.students = students;
    }

    public Set<Quiz> getQuizzes() {
        return quizzes;
    }

    public void setQuizzes(Set<Quiz> quizzes) {
        this.quizzes = quizzes;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}
