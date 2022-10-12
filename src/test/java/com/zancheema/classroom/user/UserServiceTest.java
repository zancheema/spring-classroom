package com.zancheema.classroom.user;


import com.zancheema.classroom.auth.dto.CreatedUser;
import com.zancheema.classroom.auth.dto.SignupPayload;
import com.zancheema.classroom.user.dto.UserProfile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    public void setup() {
        user = new User();
        user.setId(1);
        user.setEmail("john@example.com");
        user.setPassword("password");
        user.setFirstName("John");
        user.setLastName("Doe");
    }

    @Test
    public void nonExistingUserIdShouldReturnEmptyUserProfile() {
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.empty());

        Optional<UserProfile> userProfile = userService.getUserProfile(1);

        assertThat(userProfile).isEmpty();
    }

    @Test
    public void validUserIdShouldReturnUserProfile() {
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        when(userMapper.toUserProfile(user))
                .thenReturn(
                        new UserProfile(user.getFirstName(), user.getLastName(), user.getEmail())
                );

        Optional<UserProfile> result = userService.getUserProfile(user.getId());

        assertThat(result).isPresent();
        UserProfile profile = result.get();
        assertThat(profile.email()).isEqualTo(user.getEmail());
        assertThat(profile.firstName()).isEqualTo(user.getFirstName());
        assertThat(profile.lastName()).isEqualTo(user.getLastName());
    }

    @Test
    public void createUserFailureReturnsEmptyCreatedUser() {
        when(userRepository.existsByEmail("user@host.com"))
                .thenReturn(true);

        Optional<CreatedUser> createdUser = userService
                .createUser(new SignupPayload("first", "last", "user@host.com", "1234"));

        assertThat(createdUser).isEmpty();
    }

    @Test
    public void createUserSuccessReturnsCreatedUser() {
        CreatedUser createdUser = new CreatedUser(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail()
        );
        when(userMapper.toUser(any()))
                .thenReturn(user);
        when(userRepository.existsByEmail(user.getEmail()))
                .thenReturn(false);
        when(userRepository.save(user))
                .thenReturn(user);
        when(userMapper.toCreatedUser(user))
                .thenReturn(createdUser);

        Optional<CreatedUser> result = userService.createUser(new SignupPayload(
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPassword()
        ));

        assertThat(result)
                .isNotEmpty()
                .contains(createdUser);
    }
}