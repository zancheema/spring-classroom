package com.zancheema.classroom.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zancheema.classroom.auth.AuthController;
import com.zancheema.classroom.auth.dto.CreatedUser;
import com.zancheema.classroom.auth.dto.SignupPayload;
import com.zancheema.classroom.classroom.ClassroomController;
import com.zancheema.classroom.classroom.ClassroomService;
import com.zancheema.classroom.classroom.dto.ClassroomCreationPayload;
import com.zancheema.classroom.classroom.dto.ClassroomInfo;
import com.zancheema.classroom.classroom.dto.Teacher;
import com.zancheema.classroom.user.UserRepository;
import com.zancheema.classroom.user.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({AuthController.class, ClassroomController.class})
@Import(SecurityConfig.class)
class SecurityConfigTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private UserService userService;

    @MockBean
    private ClassroomService classroomService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void postRequestShouldRequireCsrfToken() throws Exception {
        mockMvc.perform(post("/auth/signup"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void postRequestToAuthEndpointIsPermittedToAll() throws Exception {
        SignupPayload payload = new SignupPayload("john", "doe", "john@mail.com", "pass");
        CreatedUser createdUser = new CreatedUser(1L, "john", "doe", "mail");
        when(userService.createUser(payload))
                .thenReturn(Optional.of(createdUser));

        String payloadJson = objectMapper.writeValueAsString(payload);
        mockMvc.perform(
                        post("/auth/signup")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(payloadJson)
                )
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void anyRequestToApiEndpointWithoutAuthorizationShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/api/classrooms/" + 1 + "/info"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void anyGetRequestToApiEndpointWithoutReadAuthorityShouldReturnForbidden() throws Exception {
        mockMvc.perform(
                        get("/api/classrooms/" + 1 + "/info")
                                .with(user("user"))
                )
                .andExpect(status().isForbidden());
    }

    @Test
    public void anyGetRequestToApiEndpointWithReadAuthorityShouldSucceed() throws Exception {
        ClassroomInfo classroomInfo = new ClassroomInfo(
                1, new Teacher(2, "john", "doe"), "title", "subject"
        );
        when(classroomService.findClassroomById(anyLong()))
                .thenReturn(Optional.of(classroomInfo));

        String classroomJson = objectMapper.writeValueAsString(classroomInfo);
        mockMvc.perform(
                        get("/api/classrooms/" + 1 + "/info")
                                .with(
                                        user("user").authorities(
                                                new SimpleGrantedAuthority("read")
                                        )
                                )
                )
                .andExpect(status().isOk())
                .andExpect(content().json(classroomJson));
    }

    @Test
    public void anyOtherRequestToApiEndpointWithoutWriteAuthorityShouldReturnForbidden() throws Exception {
        mockMvc.perform(
                        post("/api/classrooms/add")
                                .with(csrf())
                                .with(user("user"))
                )
                .andExpect(status().isForbidden());
    }

    @Test
    public void anyOtherRequestToApiEndpointWithWriteAuthorityShouldSucceed() throws Exception {
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
                                .with(user("user").authorities(
                                        new SimpleGrantedAuthority("write")
                                ))
                                .contentType(APPLICATION_JSON)
                                .content(payloadJson)
                )
                .andExpect(status().isCreated())
                .andExpect(content().json(classroomInfoJson));
    }
}