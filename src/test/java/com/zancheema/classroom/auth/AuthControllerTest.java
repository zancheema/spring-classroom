package com.zancheema.classroom.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zancheema.classroom.auth.dto.CreatedUser;
import com.zancheema.classroom.auth.dto.SignupPayload;
import com.zancheema.classroom.config.SecurityConfig;
import com.zancheema.classroom.user.UserRepository;
import com.zancheema.classroom.user.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    // to satisfy dependency requirement of SecurityConfig
    @MockBean
    private UserRepository userRepository;

    @MockBean
    private UserService userService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void contextLoads() {
        assertThat(mockMvc).isNotNull();
        assertThat(userService).isNotNull();
    }

    @Test
    public void unsuccessfulUserCreationShouldReturnStatus400() throws Exception {
        SignupPayload payload = new SignupPayload("john", "doe", "john@example.com", "password");

        when(userService.createUser(payload))
                .thenReturn(Optional.empty());

        mockMvc.perform(
                        post("/auth/signup")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void successfulUserCreationShouldReturnCreatedUser() throws Exception {
        SignupPayload payload = new SignupPayload("john", "doe", "john@example.com", "password");
        CreatedUser createdUser = new CreatedUser(1, payload.getFirstName(), payload.getLastName(), payload.getEmail());

        when(userService.createUser(payload))
                .thenReturn(Optional.of(createdUser));

        mockMvc.perform(
                        post("/auth/signup")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isCreated())
                .andExpect(content().string(objectMapper.writeValueAsString(createdUser)));
    }
}