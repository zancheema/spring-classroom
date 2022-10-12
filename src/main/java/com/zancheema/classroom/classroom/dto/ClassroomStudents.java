package com.zancheema.classroom.classroom.dto;

import java.util.List;

public record ClassroomStudents(long classroomId, List<Student> students) {
}
