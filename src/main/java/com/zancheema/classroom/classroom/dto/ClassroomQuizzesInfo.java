package com.zancheema.classroom.classroom.dto;

import com.zancheema.classroom.quiz.dto.QuizInfo;

import java.util.Set;

public record ClassroomQuizzesInfo(
        long classroomId,
        Set<QuizInfo> quizInfos
) {
}
