package com.zancheema.classroom.classroom;

import com.zancheema.classroom.classroom.dto.*;
import com.zancheema.classroom.quiz.Quiz;
import com.zancheema.classroom.user.User;

import java.util.Set;
import java.util.stream.Collectors;

public class ClassroomMapper {
    public ClassroomInfo toClassroomInfo(Classroom classroom) {
        User teacher = classroom.getTeacher();
        return new ClassroomInfo(
                classroom.getId(),
                new Teacher(teacher.getId(), teacher.getFirstName(), teacher.getLastName()),
                classroom.getTitle(),
                classroom.getSubject()
        );
    }

    public ClassroomStudents getClassroomStudents(Classroom classroom) {
        return new ClassroomStudents(
                classroom.getId(),
                classroom.getStudents()
                        .stream()
                        .map(this::toStudent)
                        .collect(Collectors.toList())
        );
    }

    private Student toStudent(User user) {
        return new Student(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail());
    }

    public Classroom toClassroom(String title, String subject) {
        Classroom classroom = new Classroom();
        classroom.setTitle(title);
        classroom.setSubject(subject);
        return classroom;
    }

    public ClassroomQuizzesInfo toClassroomQuizzesInfo(Classroom classroom) {
        Set<ClassroomQuizzesInfo.QuizInfo> quizInfos = classroom.getQuizzes()
                .stream()
                .map(this::toQuizInfo)
                .collect(Collectors.toSet());
        return new ClassroomQuizzesInfo(classroom.getId(), quizInfos);
    }

    private ClassroomQuizzesInfo.QuizInfo toQuizInfo(Quiz quiz) {
        return new ClassroomQuizzesInfo.QuizInfo(
                quiz.getId(), quiz.getUploadedAt(), quiz.getDeadline(),quiz.getDuration()
        );
    }
}
