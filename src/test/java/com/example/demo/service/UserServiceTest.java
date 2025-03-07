package com.example.demo.service;

import com.example.demo.dto.UserInfoResponse;
import com.example.demo.dto.UserRegistrationRequest;
import com.example.demo.entity.User;
import com.example.demo.entity.UserDetail;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.projection.UserProjection;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

public class UserServiceTest {

    private UserRepository userRepository;
    private UserService userService;

    @BeforeEach
    public void setup() {
        userRepository = Mockito.mock(UserRepository.class);
        userService = new UserService(userRepository);
    }

    @Test
    public void testRegisterUser() {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setUsername("newuser");
        request.setPassword("pass");
        request.setFirstname("First");
        request.setLastname("Last");
        request.setEmail("newuser@example.com");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("newuser");
        savedUser.setPassword("pass");
        savedUser.setStatus(false);

        Mockito.when(userRepository.save(any(User.class))).thenReturn(savedUser);

        User result = userService.registerUser(request);
        assertNotNull(result);
        assertEquals("newuser", result.getUsername());
    }

    @Test
    public void testDeleteUserSuccess() {
        Mockito.when(userRepository.existsById(1L)).thenReturn(true);
        userService.deleteUser(1L);
        Mockito.verify(userRepository).deleteById(1L);
    }

    @Test
    public void testDeleteUserNotFound() {
        Mockito.when(userRepository.existsById(2L)).thenReturn(false);
        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(2L));
    }

    @Test
    public void testUpdateUserStatus() {
        User user = new User();
        user.setId(1L);
        user.setStatus(false);
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        user.setStatus(true);
        Mockito.when(userRepository.save(user)).thenReturn(user);

        User updatedUser = userService.updateUserStatus(1L, true);
        assertTrue(updatedUser.isStatus());
    }

    @Test
    public void testGetAllUsers() {
        // Create anonymous implementations of UserProjection
        UserProjection projection1 = new UserProjection() {
            @Override
            public Long getId() {
                return 1L;
            }
            @Override
            public String getUsername() {
                return "user1";
            }
        };
        UserProjection projection2 = new UserProjection() {
            @Override
            public Long getId() {
                return 2L;
            }
            @Override
            public String getUsername() {
                return "user2";
            }
        };

        // Since userService.getAllUsers() now calls userRepository.findAllBy(),
        // we configure the mock accordingly.
        Mockito.when(userRepository.findAllBy()).thenReturn(Arrays.asList(projection1, projection2));

        List<UserProjection> projections = userService.getAllUsers();
        assertEquals(2, projections.size());
        assertEquals("user1", projections.get(0).getUsername());
    }

    @Test
    public void testGetUserInfoSuccess() {
        User user = new User();
        user.setId(1L);
        user.setUsername("user1");

        UserDetail detail = new UserDetail();
        detail.setFirstname("First");
        detail.setLastname("Last");
        detail.setEmail("user1@example.com");
        user.setUserDetail(detail);

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserInfoResponse response = userService.getUserInfo(1L);
        assertNotNull(response);
        assertEquals("user1", response.getUsername());
        assertEquals("First", response.getFirstname());
    }

    @Test
    public void testGetUserInfoNotFound() {
        Mockito.when(userRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.getUserInfo(99L));
    }
}
