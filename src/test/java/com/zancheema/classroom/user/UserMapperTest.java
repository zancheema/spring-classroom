package com.zancheema.classroom.user;


import com.zancheema.classroom.auth.dto.CreatedUser;
import com.zancheema.classroom.auth.dto.SignupPayload;
import com.zancheema.classroom.user.dto.UserProfile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserMapperTest {
    private UserMapper userMapper;

    @BeforeEach
    public void setup() {
        userMapper = new UserMapper();
    }

    @Test
    public void toUserProfileForUserObjectShouldReturnUserProfileObject() {
        User user = new User();
        user.setFirstName("john");
        user.setLastName("jane");
        user.setEmail("user@example.com");

        UserProfile profile = userMapper.toUserProfile(user);

        assertThat(profile).isNotNull();
        assertThat(profile.firstName()).isEqualTo(user.getFirstName());
        assertThat(profile.lastName()).isEqualTo(user.getLastName());
        assertThat(profile.email()).isEqualTo(user.getEmail());
    }

    @Test
    public void toCreatedUserForUserShouldReturnCreatedUserObject() {
        User user = new User();
        user.setId(52L);
        user.setFirstName("john");
        user.setLastName("jane");
        user.setEmail("user@example.com");

        CreatedUser createdUser = userMapper.toCreatedUser(user);

        assertThat(createdUser).isNotNull();
        assertThat(createdUser.id()).isEqualTo(user.getId());
        assertThat(createdUser.firstName()).isEqualTo(user.getFirstName());
        assertThat(createdUser.lastName()).isEqualTo(user.getLastName());
        assertThat(createdUser.email()).isEqualTo(user.getEmail());
    }

    @Test
    public void toUserForSignUpPayloadShouldReturnUserObject() {
        SignupPayload payload = new SignupPayload("john", "doe", "john@example.com", "pass");

        User user = userMapper.toUser(payload);

        assertThat(user).isNotNull();
        assertThat(user.getFirstName()).isEqualTo(payload.getFirstName());
        assertThat(user.getLastName()).isEqualTo(payload.getLastName());
        assertThat(user.getEmail()).isEqualTo(payload.getEmail());
        assertThat(user.getPassword()).isEqualTo(payload.getPassword());
    }
}