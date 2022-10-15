package com.zancheema.classroom.classroom.dto;

import java.util.Set;

public record AttendingClassrooms(long studentId, Set<ClassroomBody> attendingClassrooms) {
}
