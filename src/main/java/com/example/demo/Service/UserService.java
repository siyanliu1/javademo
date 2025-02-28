package com.example.demo.Service;

import com.example.demo.dto.UserInfoResponse;
import com.example.demo.dto.UserRegistrationRequest;
import com.example.demo.entity.User;
import com.example.demo.entity.UserDetail;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.projection.UserProjection;
import com.example.demo.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User registerUser(UserRegistrationRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setStatus(false);


        UserDetail detail = new UserDetail();
        detail.setFirstname(request.getFirstname());
        detail.setLastname(request.getLastname());
        detail.setEmail(request.getEmail());

        detail.setUser(user);
        user.setUserDetail(detail);
        return userRepository.save(user);
    }

    // Delete a user by userId
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found");
        }
        userRepository.deleteById(userId);
    }

    // Activate or deactivate a user
    public User updateUserStatus(Long userId, boolean activate) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (!optionalUser.isPresent()) {
            throw new RuntimeException("User not found");
        }
        User user = optionalUser.get();
        user.setStatus(activate);
        return userRepository.save(user);
    }

    // List all users using projection (only id and username)
    public List<UserProjection> getAllUsers() {
        return userRepository.findAllBy();
    }

    // Get user info by user id
    public UserInfoResponse getUserInfo(Long userId) {
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new RuntimeException("User not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with id " + userId + " not found"));

        return new UserInfoResponse(
                user.getId(),
                user.getUsername(),
                user.getUserDetail().getFirstname(),
                user.getUserDetail().getLastname(),
                user.getUserDetail().getEmail()
        );
    }
}
