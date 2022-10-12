package com.zancheema.classroom.user;

import com.zancheema.classroom.auth.dto.CreatedUser;
import com.zancheema.classroom.auth.dto.SignupPayload;
import com.zancheema.classroom.user.dto.UserProfile;

import java.util.Optional;

public interface UserService {
    Optional<UserProfile> getUserProfile(long userId);

    Optional<CreatedUser> createUser(SignupPayload payload);
}
