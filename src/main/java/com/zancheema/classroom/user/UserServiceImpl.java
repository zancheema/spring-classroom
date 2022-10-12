package com.zancheema.classroom.user;

import com.zancheema.classroom.auth.dto.CreatedUser;
import com.zancheema.classroom.auth.dto.SignupPayload;
import com.zancheema.classroom.user.dto.UserProfile;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public Optional<UserProfile> getUserProfile(long userId) {
        return userRepository.findById(userId)
                .map(userMapper::toUserProfile);
    }

    @Override
    public Optional<CreatedUser> createUser(SignupPayload payload) {
        if (userRepository.existsByEmail(payload.getEmail())) {
            return Optional.empty();
        }

        User user = userMapper.toUser(payload);
        User savedUser = userRepository.save(user);
        CreatedUser createdUser = userMapper.toCreatedUser(savedUser);
        return Optional.of(createdUser);
    }
}
