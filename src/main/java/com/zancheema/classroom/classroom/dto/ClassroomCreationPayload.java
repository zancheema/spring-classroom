package com.zancheema.classroom.classroom.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClassroomCreationPayload {
    private long teacherId;
    private String title;
    private String subject;
}
