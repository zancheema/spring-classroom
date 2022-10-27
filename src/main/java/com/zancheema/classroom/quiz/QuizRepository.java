package com.zancheema.classroom.quiz;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface QuizRepository extends CrudRepository<Quiz, Long> {
    Optional<Quiz> findByIdAndClassroomId(Long quizId, long classroomId);
}
