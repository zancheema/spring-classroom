package com.zancheema.classroom.classroom.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;

public record ClassroomQuizzesInfo(
        long classroomId,
        Set<QuizInfo> quizInfos
) {
    public record QuizInfo(
            long id,

            @JsonSerialize(using = LocalDateTimeSerializer.class)
            @JsonDeserialize(using = LocalDateTimeDeserializer.class)
            LocalDateTime uploadedAt,

            @JsonSerialize(using = LocalDateTimeSerializer.class)
            @JsonDeserialize(using = LocalDateTimeDeserializer.class)
            LocalDateTime deadline,

            @JsonSerialize(using = LocalTimeSerializer.class)
            @JsonDeserialize(using = LocalTimeDeserializer.class)
            LocalTime duration
    ) {
    }
}
