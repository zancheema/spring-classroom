package com.zancheema.classroom.classroom;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zancheema.classroom.classroom.dto.*;
import com.zancheema.classroom.classroom.dto.ClassroomQuiz.QuizQuestion;
import com.zancheema.classroom.config.SecurityConfig;
import com.zancheema.classroom.quiz.dto.QuizInfo;
import com.zancheema.classroom.user.User;
import com.zancheema.classroom.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.zancheema.classroom.common.Formats.DATE_FORMAT;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ClassroomController.class)
@Import(SecurityConfig.class)
public class ClassroomControllerTest {
    @MockBean
    private ClassroomService classroomService;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .setDateFormat(new SimpleDateFormat(DATE_FORMAT));

    private final String authenticatedUsername = "john@example.com";
    @Mock
    private User authenticatedUser;

    @BeforeEach
    public void setup() {
        authenticatedUser = new User();
        authenticatedUser.setEmail(authenticatedUsername);
        authenticatedUser.setAuthorities(List.of(new SimpleGrantedAuthority("read")));

        when(userRepository.findByEmail(authenticatedUsername))
                .thenReturn(Optional.of(authenticatedUser));
    }

    @Test
    public void getClassroomWithoutAuthorizationShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/api/classrooms/" + 1 + "/info"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    public void getClassroomWithoutReadAuthorityShouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/api/classrooms/" + 1 + "/info"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "read")
    public void getClassroomWithInvalidClassroomIdShouldReturnNotFound() throws Exception {
        when(classroomService.findClassroomById(anyLong()))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/classrooms/" + 1 + "/info"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(authorities = "read")
    public void getClassroomWithValidClassroomIdShouldReturnClassroomList() throws Exception {
        ClassroomInfo classroomInfo = new ClassroomInfo(
                1, new Teacher(2, "john", "doe"), "title", "subject"
        );
        when(classroomService.findClassroomById(anyLong()))
                .thenReturn(Optional.of(classroomInfo));

        String classroomJson = objectMapper.writeValueAsString(classroomInfo);
        mockMvc.perform(get("/api/classrooms/" + 1 + "/info"))
                .andExpect(status().isOk())
                .andExpect(content().json(classroomJson));
    }

    @Test
    public void getTeacherWithoutAuthorizationShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/api/classrooms/" + 1 + "/teacher"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    public void getTeacherWithoutReadAuthorityShouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/api/classrooms/" + 1 + "/teacher"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "read")
    public void getTeacherWithInvalidClassroomIdShouldReturnNotFound() throws Exception {
        when(classroomService.findTeacher(anyLong()))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/classrooms/" + 1 + "/teacher"))
                .andExpectAll(status().isNotFound());
    }

    @Test
    @WithMockUser(authorities = "read")
    public void getTeacherWithValidClassroomIdShouldReturnTeacherObject() throws Exception {
        Teacher teacher = new Teacher(1, "jane", "doe");
        when(classroomService.findTeacher(teacher.id()))
                .thenReturn(Optional.of(teacher));

        String teacherJson = objectMapper.writeValueAsString(teacher);
        mockMvc.perform(get("/api/classrooms/" + 1 + "/teacher"))
                .andExpect(status().isOk())
                .andExpect(content().json(teacherJson));
    }

    @Test
    public void getStudentsWithoutAuthorizationShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/api/classrooms/" + 1 + "/students"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    public void getStudentsWithoutReadAuthorityShouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/api/classrooms/" + 1 + "/students"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "read")
    public void getStudentsFailureShouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/classrooms/" + 1 + "/students"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(authorities = "read")
    public void getStudentsSuccessShouldReturnStudentList() throws Exception {
        ClassroomStudents classroomStudents = new ClassroomStudents(
                1,
                List.of(new Student(1, "first", "last", "authenticatedUser@host.com"))
        );
        when(classroomService.findClassroomStudents(classroomStudents.classroomId()))
                .thenReturn(Optional.of(classroomStudents));

        String classroomStudentsJson = objectMapper.writeValueAsString(classroomStudents);
        mockMvc.perform(get("/api/classrooms/" + classroomStudents.classroomId() + "/students"))
                .andExpect(status().isOk())
                .andExpect(content().json(classroomStudentsJson));
    }

    @Test
    public void getAttendingClassroomsWithoutAuthenticationShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/api/classrooms/attending"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    public void getAttendingClassroomsWithoutReadAuthorityShouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/api/classrooms/attending"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails(value = authenticatedUsername, setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void getAttendingClassroomsShouldReturnClassroomsAttendedByUserAsStudent() throws Exception {
        AttendingClassrooms attendingClassrooms = new AttendingClassrooms(1L, Set.of(
                new ClassroomInfo(2L, new Teacher(3L, "first", "last"), "a", "b"),
                new ClassroomInfo(4L, new Teacher(5L, "a", "n"), "x", "z")
        ));
        when(classroomService.findAttendingClassrooms(authenticatedUsername))
                .thenReturn(Optional.of(attendingClassrooms));

        String attendingClassroomsJson = objectMapper.writeValueAsString(attendingClassrooms);
        mockMvc.perform(get("/api/classrooms/attending"))
                .andExpect(status().isOk())
                .andExpect(content().json(attendingClassroomsJson));
    }

    @Test
    public void createClassroomWithoutCsrfShouldReturnForbidden() throws Exception {
        mockMvc.perform(post("/api/classrooms/add"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void createClassroomWithoutCsrfTokenShouldReturnForbidden() throws Exception {
        mockMvc.perform(
                        post("/api/classrooms/add")
                )
                .andExpect(status().isForbidden());
    }

    @Test
    public void createClassroomWithoutAuthorizationShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(
                        post("/api/classrooms/add")
                                .with(csrf())
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    public void createClassroomWithoutReadAuthorityShouldReturnForbidden() throws Exception {
        ClassroomCreationPayload payload = new ClassroomCreationPayload(1, "title", "sub");
        String payloadJson = objectMapper.writeValueAsString(payload);

        mockMvc.perform(
                        post("/api/classrooms/add")
                                .with(csrf())
                                .contentType(APPLICATION_JSON)
                                .content(payloadJson)
                )
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "write")
    public void createClassroomInvalidShouldReturnBadRequest() throws Exception {
        ClassroomCreationPayload payload = new ClassroomCreationPayload(1, "title", "sub");
        String payloadJson = objectMapper.writeValueAsString(payload);
        when(classroomService.createClassroom(payload))
                .thenReturn(Optional.empty());

        mockMvc.perform(
                        post("/api/classrooms/add")
                                .with(csrf())
                                .contentType(APPLICATION_JSON)
                                .content(payloadJson)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(authorities = "write")
    public void createClassroomSuccessShouldReturnCreatedStatusAndClassroom() throws Exception {
        ClassroomCreationPayload payload = new ClassroomCreationPayload(1, "title", "sub");
        String payloadJson = objectMapper.writeValueAsString(payload);
        ClassroomInfo classroomInfo = new ClassroomInfo(
                2, new Teacher(1, "first", "last"), "title", "sub"
        );
        String classroomInfoJson = objectMapper.writeValueAsString(classroomInfo);
        when(classroomService.createClassroom(payload))
                .thenReturn(Optional.of(classroomInfo));

        mockMvc.perform(
                        post("/api/classrooms/add")
                                .with(csrf())
                                .contentType(APPLICATION_JSON)
                                .content(payloadJson)
                )
                .andExpect(status().isCreated())
                .andExpect(content().json(classroomInfoJson));
    }

    @Test
    public void addStudentToClassroomWithoutCsrfShouldReturnForbidden() throws Exception {
        mockMvc.perform(
                        post("/api/classrooms/add/student/")
                )
                .andExpect(status().isForbidden());
    }

    @Test
    public void addStudentToClassroomWithoutAuthorizationShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(
                        post("/api/classrooms/add/student/")
                                .with(csrf())
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    public void addStudentToClassroomWithoutWriteAuthorityShouldReturnForbidden() throws Exception {
        ClassroomStudent classroomStudent = new ClassroomStudent(1L, 2L);
        String classroomStudentJson = objectMapper.writeValueAsString(classroomStudent);
        mockMvc.perform(
                        post("/api/classrooms/add/student")
                                .with(csrf())
                                .contentType(APPLICATION_JSON)
                                .content(classroomStudentJson)
                )
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "write")
    public void addStudentToClassroomFailureShouldReturnBadRequest() throws Exception {
        ClassroomStudent classroomStudent = new ClassroomStudent(1L, 2L);
        String classroomStudentJson = objectMapper.writeValueAsString(classroomStudent);
        when(classroomService.addStudentToClassroom(classroomStudent))
                .thenReturn(Optional.empty());

        mockMvc.perform(
                        post("/api/classrooms/add/student/")
                                .with(csrf())
                                .contentType(APPLICATION_JSON)
                                .content(classroomStudentJson)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(authorities = "write")
    public void addStudentToClassroomSuccessShouldReturnClassroomStudentObject() throws Exception {
        ClassroomStudent classroomStudent = new ClassroomStudent(1L, 2L);
        String classroomStudentJson = objectMapper.writeValueAsString(classroomStudent);
        when(classroomService.addStudentToClassroom(classroomStudent))
                .thenReturn(Optional.of(classroomStudent));

        mockMvc.perform(
                        post("/api/classrooms/add/student")
                                .with(csrf())
                                .contentType(APPLICATION_JSON)
                                .content(classroomStudentJson)
                )
                .andExpect(status().isCreated())
                .andExpect(content().json(classroomStudentJson));
    }

    @Test
    public void updateClassroomWithoutCsrfShouldReturnForbidden() throws Exception {
        mockMvc.perform(patch("/api/classrooms/update/" + 1))
                .andExpect(status().isForbidden());
    }

    @Test
    public void updateClassroomWithoutAuthorizationShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(
                        patch("/api/classrooms/update/" + 1)
                                .with(csrf())
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    public void updateClassroomWithoutWriteAuthorityShouldReturnForbidden() throws Exception {
        long classroomId = 2;
        UpdateClassroomPayload payload = new UpdateClassroomPayload(1L, "title", "sub");
        String payloadJson = objectMapper.writeValueAsString(payload);
        mockMvc.perform(
                        patch("/api/classrooms/update/" + classroomId)
                                .with(csrf())
                                .contentType(APPLICATION_JSON)
                                .content(payloadJson)
                )
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "write")
    public void updatedClassroomFailureShouldReturnBadRequest() throws Exception {
        long classroomId = 2;
        UpdateClassroomPayload payload = new UpdateClassroomPayload(1L, "title", "sub");
        String payloadJson = objectMapper.writeValueAsString(payload);
        when(classroomService.updateClassroom(classroomId, payload))
                .thenReturn(Optional.empty());

        mockMvc.perform(
                        patch("/api/classrooms/update/" + classroomId)
                                .with(csrf())
                                .contentType(APPLICATION_JSON)
                                .content(payloadJson)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(authorities = "write")
    public void updateClassroomSuccessShouldReturnUpdatedClassroomObject() throws Exception {
        long classroomId = 2;
        UpdateClassroomPayload payload = new UpdateClassroomPayload(1L, "title", "sub");
        String payloadJson = objectMapper.writeValueAsString(payload);
        Teacher teacher = new Teacher(1, "first", "last");
        ClassroomInfo classroomInfo = new ClassroomInfo(2, teacher, "title", "subject");
        String classroomInfoJson = objectMapper.writeValueAsString(classroomInfo);
        when(classroomService.updateClassroom(classroomId, payload))
                .thenReturn(Optional.of(classroomInfo));

        mockMvc.perform(
                        patch("/api/classrooms/update/" + classroomId)
                                .with(csrf())
                                .contentType(APPLICATION_JSON)
                                .content(payloadJson)
                )
                .andExpect(status().isOk())
                .andExpect(content().json(classroomInfoJson));
    }

    @Test
    public void getClassroomQuizzesInfoWithoutAuthorizationShouldUnauthorized() throws Exception {
        mockMvc.perform(get("/api/classrooms/" + 1 + "/quizzes/info"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    public void getClassroomQuizzesInfoWithoutReadAuthorityShouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/api/classrooms/" + 1 + "/quizzes/info"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "read")
    public void getClassroomQuizzesInfoNonExistentClassroomShouldReturnNotFound() throws Exception {
        when(classroomService.findClassroomQuizInfos(1L))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/classrooms/" + 1 + "/quizzes/info"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(authorities = "read")
    public void getClassroomQuizzesInfoShouldReturnClassroomQuizzesObject() throws Exception {
        ClassroomQuizzesInfo classroomQuizzesInfo = new ClassroomQuizzesInfo(
                1L, Set.of(
                new QuizInfo(2L, LocalDateTime.now(), LocalDateTime.now(), LocalTime.of(9, 20)),
                new QuizInfo(3L, LocalDateTime.now(), LocalDateTime.now(), LocalTime.of(9, 30))
        ));
        String classroomQuizzesInfoJson = objectMapper.writeValueAsString(classroomQuizzesInfo);
        when(classroomService.findClassroomQuizInfos(1L))
                .thenReturn(Optional.of(classroomQuizzesInfo));

        mockMvc.perform(get("/api/classrooms/" + 1 + "/quizzes/info"))
                .andExpect(status().isOk())
                .andExpect(content().json(classroomQuizzesInfoJson));
    }

    @Test
    public void getClassroomQuizWithoutAuthorizationShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/api/classrooms/" + 1 + "/quiz/" + 2))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    public void getClassroomQuizWithoutReadAuthorityShouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/api/classrooms/" + 1 + "/quiz/" + 2))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "read")
    public void getNotExistentClassroomQuizShouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/classrooms/" + 1 + "/quiz/" + 2))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(authorities = "read")
    public void getClassroomQuizShouldReturnClassroomQuizObject() throws Exception {
        ClassroomQuiz quiz = new ClassroomQuiz(
                1L,
                2L,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1),
                LocalTime.of(0, 30),
                Set.of(
                        new QuizQuestion(3L, 1, "Q1", Set.of(
                                new QuizQuestion.Answer(4L, 1, "A"),
                                new QuizQuestion.Answer(5L, 2, "B"),
                                new QuizQuestion.Answer(6L, 3, "C")
                        )),
                        new QuizQuestion(7L, 1, "Q2", Set.of(
                                new QuizQuestion.Answer(8L, 1, "A"),
                                new QuizQuestion.Answer(9L, 2, "B"),
                                new QuizQuestion.Answer(10L, 3, "C")
                        ))
                )
        );
        when(classroomService.findClassroomQuiz(quiz.classroomId(), quiz.quizId()))
                .thenReturn(Optional.of(quiz));

        String quizJson = objectMapper.writeValueAsString(quiz);
        mockMvc.perform(get("/api/classrooms/" + quiz.classroomId() + "/quiz/" + quiz.quizId()))
                .andExpect(status().isOk())
                .andExpect(content().json(quizJson));
    }
}
