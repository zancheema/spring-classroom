package com.zancheema.classroom.classroom;

import com.zancheema.classroom.classroom.dto.ClassroomBody;
import com.zancheema.classroom.classroom.dto.ClassroomStudents;
import com.zancheema.classroom.classroom.dto.Student;
import com.zancheema.classroom.classroom.dto.Teacher;
import com.zancheema.classroom.user.User;

import java.util.stream.Collectors;

public class ClassroomMapper {
    public ClassroomBody toClassroomBody(Classroom classroom) {
        User teacher = classroom.getTeacher();
        return new ClassroomBody(
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
}
