package com.zancheema.classroom.classroom.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizSubmissionPayload {
    private @NotNull Long quizId;
    private @NotEmpty Set<SubmittedAnswer> answers;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SubmittedAnswer {
        private @NotNull long questionId;
        private @NotNull long answerId;
    }
}
