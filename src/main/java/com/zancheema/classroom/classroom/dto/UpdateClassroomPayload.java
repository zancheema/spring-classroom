package com.zancheema.classroom.classroom.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateClassroomPayload {
    private int teacherId;
    private String title;
    private String subject;
}
