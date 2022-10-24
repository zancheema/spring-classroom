package com.zancheema.classroom.classroom;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ClassroomRepository extends CrudRepository<Classroom, Long> {
    List<Classroom> findByStudentsId(long studentId);
}
