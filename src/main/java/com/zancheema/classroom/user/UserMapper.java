package com.zancheema.classroom.user;

import com.zancheema.classroom.auth.dto.CreatedUser;
import com.zancheema.classroom.auth.dto.SignupPayload;
import com.zancheema.classroom.user.dto.UserProfile;

public class UserMapper {
    public UserProfile toUserProfile(User user) {
        return new UserProfile(user.getFirstName(), user.getLastName(), user.getEmail());
    }

    public CreatedUser toCreatedUser(User user) {
        return new CreatedUser(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail());
    }

    public User toUser(SignupPayload payload) {
        User user = new User();
        user.setFirstName(payload.getFirstName());
        user.setLastName(payload.getLastName());
        user.setEmail(payload.getEmail());
        user.setPassword(payload.getPassword());
        return user;
    }
}
