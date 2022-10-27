package com.zancheema.classroom.quiz;

import com.zancheema.classroom.classroom.Classroom;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
public class Quiz {
    @Id
    @GeneratedValue
    private long id;

    @ManyToOne(optional = false)
    private Classroom classroom;

    @Column(nullable = false)
    private LocalDateTime uploadedAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime deadline;

    @Column(nullable = false)
    private LocalTime duration;

    public Quiz() {
    }

    public Quiz(Classroom classroom, LocalDateTime deadline, LocalTime duration) {
        this.classroom = classroom;
        this.deadline = deadline;
        this.duration = duration;
    }

    public Quiz(long id, Classroom classroom, LocalDateTime deadline, LocalTime duration) {
        this(classroom, deadline, duration);
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Classroom getClassroom() {
        return classroom;
    }

    public void setClassroom(Classroom classroom) {
        this.classroom = classroom;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

    public LocalTime getDuration() {
        return duration;
    }

    public void setDuration(LocalTime duration) {
        this.duration = duration;
    }
}
