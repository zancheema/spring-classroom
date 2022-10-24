package com.zancheema.classroom.classroom;

import com.zancheema.classroom.classroom.dto.*;
import org.springframework.http.ResponseEntity;
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
    public AttendingClassrooms getAttendingClassrooms(
            @AuthenticationPrincipal(expression = "username") String username
    ) {
        return classroomService.findAttendingClassrooms(username).get();
    }

    @GetMapping("/{classroomId}/teacher")
    public ResponseEntity<Teacher> getClassroomTeacher(@PathVariable("classroomId") long classroomId) {
        Optional<Teacher> teacher = classroomService.findTeacher(classroomId);
        return ResponseEntity.of(teacher);
    }

    @GetMapping("/{classroomId}/students")
    public ResponseEntity<ClassroomStudents> getClassroomStudents(@PathVariable("classroomId") long classroomId) {
        Optional<ClassroomStudents> students = classroomService.findClassroomStudents(classroomId);
        return ResponseEntity.of(students);
    }

    @PostMapping("/add")
    public ResponseEntity<ClassroomInfo> createClassroom(@RequestBody @Valid ClassroomCreationPayload payload) {
        Optional<ClassroomInfo> classroom = classroomService.createClassroom(payload);
        if (classroom.isEmpty()) return ResponseEntity.badRequest().build();

        return ResponseEntity
                .created(null)
                .body(classroom.get());
    }

    @PostMapping("{classroomId}/add/student")
    public ResponseEntity<ClassroomStudent> addStudentToClassroom(
            @PathVariable("classroomId") long classroomId,
            @Valid @RequestBody ClassroomStudent classroomStudent
    ) {
        Optional<ClassroomStudent> result = classroomService.addStudentToClassroom(classroomId, classroomStudent);
        if (result.isEmpty()) return ResponseEntity.badRequest().build();
        return ResponseEntity.created(null).body(result.get());
    }

    @PatchMapping("/{classroomId}/update")
    public ResponseEntity<ClassroomInfo> updateClassroom(
            @PathVariable("classroomId") long classroomId,
            @Valid @RequestBody UpdateClassroomPayload payload
    ) {
        Optional<ClassroomInfo> classroomBody = classroomService.updateClassroom(classroomId, payload);
        if (classroomBody.isEmpty()) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(classroomBody.get());
    }

    @GetMapping("/{classroomId}/quizzes/info")
    public ResponseEntity<ClassroomQuizzesInfo> getClassroomQuizzesInfos(
            @PathVariable("classroomId") long classroomId
    ) {
        Optional<ClassroomQuizzesInfo> infos = classroomService.findClassroomQuizzesInfo(classroomId);
        return ResponseEntity.of(infos);
    }

    @GetMapping("/{classroomId}/quiz/{quizId}")
    public ResponseEntity<ClassroomQuiz> getClassroomQuiz(
            @PathVariable("classroomId") long classroomId,
            @PathVariable("quizId") long quizId
    ) {
        Optional<ClassroomQuiz> classroomQuiz = classroomService.findClassroomQuiz(classroomId, quizId);
        return ResponseEntity.of(classroomQuiz);
    }

    @PostMapping("/{classroomId}/quiz/submit")
    public ResponseEntity<?> submitQuiz(
            @PathVariable("classroomId") long classroomId,
            @Valid @RequestBody QuizSubmissionPayload payload
    ) {
        boolean successful = classroomService.submitQuiz(classroomId, payload);
        ResponseEntity.BodyBuilder response = successful
                ? ResponseEntity.ok()
                : ResponseEntity.badRequest();
        return response.build();
    }
}
