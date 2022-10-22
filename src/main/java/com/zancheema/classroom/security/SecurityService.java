package com.zancheema.classroom.security;

public interface SecurityService {
    boolean classHasTheTeacher(long classroomId, long teacherId);

    boolean classHasTheStudent(long classroomId, long studentId);
}
