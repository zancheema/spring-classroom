package com.zancheema.classroom.classroom;

import com.zancheema.classroom.classroom.dto.*;

import java.util.Optional;

public interface ClassroomService {
    Optional<ClassroomInfo> findClassroomById(long classroomId);

    Optional<Teacher> findTeacher(long classroomId);

    Optional<ClassroomStudents> findClassroomStudents(long classroomId);

    Optional<ClassroomInfo> createClassroom(ClassroomCreationPayload payload);

    Optional<ClassroomStudent> addStudentToClassroom(ClassroomStudent payload);

    Optional<ClassroomInfo> updateClassroom(long classroomId, UpdateClassroomPayload payload);

    Optional<AttendingClassrooms> findAttendingClassrooms(String username);

    Optional<ClassroomQuizzesInfo> findClassroomQuizInfos(long classroomId);

    Optional<ClassroomQuiz> findClassroomQuiz(long classroomId, long quizId);
}
