package com.zancheema.classroom.classroom;

import com.zancheema.classroom.classroom.dto.*;

import java.util.Optional;

public interface ClassroomService {
    Optional<ClassroomBody> findClassroomById(long anyLong);

    Optional<Teacher> findTeacher(long classroomId);

    Optional<ClassroomStudents> findClassroomStudents(long classroomId);

    Optional<ClassroomBody> createClassroom(ClassroomCreationPayload payload);

    Optional<ClassroomStudent> addStudentToClassroom(ClassroomStudent payload);

    Optional<ClassroomBody> updateClassroom(long classroomId, UpdateClassroomPayload payload);
}
