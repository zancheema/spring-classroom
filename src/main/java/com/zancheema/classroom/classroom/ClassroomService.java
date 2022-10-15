package com.zancheema.classroom.classroom;

import com.zancheema.classroom.classroom.dto.*;

import java.util.Optional;

public interface ClassroomService {
    Optional<ClassroomBody> findClassroomById(long classroomId);

    Optional<Teacher> findTeacher(long classroomId);

    Optional<ClassroomStudents> findClassroomStudents(long classroomId);

    Optional<ClassroomBody> createClassroom(ClassroomCreationPayload payload);

    Optional<ClassroomStudent> addStudentToClassroom(ClassroomStudent payload);

    Optional<ClassroomBody> updateClassroom(long classroomId, UpdateClassroomPayload payload);

    Optional<AttendingClassrooms> findAttendingClassrooms(String username);
}
