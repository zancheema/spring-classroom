package com.zancheema.classroom.classroom;

import com.zancheema.classroom.classroom.dto.ClassroomBody;
import com.zancheema.classroom.classroom.dto.ClassroomStudents;

public class ClassroomMapper {
    public ClassroomBody toClassroomBody(Classroom classroomObj) {
        return null;
    }

    public ClassroomStudents getClassroomStudents(Classroom classroomObj) {
        return null;
    }

    public Classroom toClassroom(String title, String subject) {
        Classroom classroom = new Classroom();
        classroom.setTitle(title);
        classroom.setSubject(subject);
        return classroom;
    }
}
