package com.zancheema.classroom.classroom;

import com.zancheema.classroom.classroom.dto.*;
import com.zancheema.classroom.quiz.dto.QuizInfo;

import java.util.Optional;



public interface ClassroomService {
    Optional<ClassroomInfo> findClassroomById(long classroomId);

    Optional<Teacher> findTeacher(long classroomId);

    Optional<ClassroomStudents> findClassroomStudents(long classroomId);

    Optional<ClassroomInfo> createClassroom(ClassroomCreationPayload payload);

    Optional<ClassroomStudent> addStudentToClassroom(long classroomId, ClassroomStudent payload);

    Optional<ClassroomInfo> updateClassroom(long classroomId, UpdateClassroomPayload payload);

    Optional<AttendingClassrooms> findAttendingClassrooms(String username);

    Optional<ClassroomQuizzesInfo> findClassroomQuizzesInfo(long classroomId);

    Optional<QuizInfo> findQuizInfo(long classroomId, long quizId);

    boolean submitQuiz(long classroomId, QuizSubmissionPayload payload);
}
