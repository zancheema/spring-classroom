package com.zancheema.classroom.classroom;

import com.zancheema.classroom.classroom.dto.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/api/classrooms")
public class ClassroomController {
    private final ClassroomService classroomService;

    public ClassroomController(ClassroomService classroomService) {
        this.classroomService = classroomService;
    }

    @GetMapping("/{classroomId}/info")
    @PreAuthorize("hasAuthority(read)")
    public ResponseEntity<ClassroomInfo> getClassroom(@PathVariable("classroomId") long classroomId) {
        Optional<ClassroomInfo> classroom = classroomService.findClassroomById(classroomId);
        return ResponseEntity.of(classroom);
    }

    /**
     * Do not need {@link Optional} because if the user does not exist,
     * they won't be authorized to reach to this endpoint,
     * and result in 401.
     */
    @GetMapping("/attending")
    @PreAuthorize("hasAuthority(read)")
    public AttendingClassrooms getAttendingClassrooms(
            @AuthenticationPrincipal(expression = "username") String username
    ) {
        return classroomService.findAttendingClassrooms(username).get();
    }

    @GetMapping("/{classroomId}/teacher")
    @PreAuthorize("hasAuthority(read)")
    public ResponseEntity<Teacher> getClassroomTeacher(@PathVariable("classroomId") long classroomId) {
        Optional<Teacher> teacher = classroomService.findTeacher(classroomId);
        return ResponseEntity.of(teacher);
    }

    @GetMapping("/{classroomId}/students")
    @PreAuthorize("hasAuthority(read)")
    public ResponseEntity<ClassroomStudents> getClassroomStudents(@PathVariable("classroomId") long classroomId) {
        Optional<ClassroomStudents> students = classroomService.findClassroomStudents(classroomId);
        return ResponseEntity.of(students);
    }

    @PostMapping("/add")
    @PreAuthorize("hasAuthority(write)")
    public ResponseEntity<ClassroomInfo> createClassroom(@RequestBody @Valid ClassroomCreationPayload payload) {
        Optional<ClassroomInfo> classroom = classroomService.createClassroom(payload);
        if (classroom.isEmpty()) return ResponseEntity.badRequest().build();

        return ResponseEntity
                .created(null)
                .body(classroom.get());
    }

    @PostMapping("/add/student")
    @PreAuthorize("hasAuthority(write)")
    public ResponseEntity<ClassroomStudent> addStudentToClassroom(
            @Valid @RequestBody ClassroomStudent classroomStudent
    ) {
        Optional<ClassroomStudent> result = classroomService.addStudentToClassroom(classroomStudent);
        if (result.isEmpty()) return ResponseEntity.badRequest().build();
        return ResponseEntity.created(null).body(result.get());
    }

    @PatchMapping("/update/{classroomId}")
    @PreAuthorize("hasAuthority(write)")
    public ResponseEntity<ClassroomInfo> updateClassroom(
            @PathVariable("classroomId") long classroomId,
            @Valid @RequestBody UpdateClassroomPayload payload
    ) {
        Optional<ClassroomInfo> classroomBody = classroomService.updateClassroom(classroomId, payload);
        if (classroomBody.isEmpty()) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(classroomBody.get());
    }

    @GetMapping("/{classroomId}/quizzes/info")
    @PreAuthorize("hasAuthority(read)")
    public ResponseEntity<ClassroomQuizzesInfo> getClassroomQuizzesInfos(
            @PathVariable("classroomId") long classroomId
    ) {
        Optional<ClassroomQuizzesInfo> infos = classroomService.findClassroomQuizInfos(classroomId);
        return ResponseEntity.of(infos);
    }

    @GetMapping("/{classroomId}/quiz/{quizId}")
    @PreAuthorize("hasAuthority(read)")
    public ResponseEntity<ClassroomQuiz> getClassroomQuiz(
            @PathVariable("classroomId") long classroomId,
            @PathVariable("quizId") long quizId
    ) {
        Optional<ClassroomQuiz> classroomQuiz = classroomService.findClassroomQuiz(classroomId, quizId);
        return ResponseEntity.of(classroomQuiz);
    }
}
