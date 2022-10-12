package com.zancheema.classroom.classroom.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateClassroomPayload {
    private @NotNull Long teacherId;
    private @NotNull String title;
    private @NotNull String subject;
}
