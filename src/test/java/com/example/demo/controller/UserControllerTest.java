package com.example.demo.controller;

import com.example.demo.dto.UserRegistrationRequest;
import com.example.demo.dto.UserInfoResponse;
import com.example.demo.projection.UserProjection;
import com.example.demo.entity.User;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import(UserControllerTest.MockUserServiceConfig.class)
public class UserControllerTest {

    @TestConfiguration
    static class MockUserServiceConfig {
        @Bean
        public UserService userService() {
            return Mockito.mock(UserService.class);
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @Test
    @WithMockUser(username = "testuser", authorities = {"PERM_WRITE", "PERM_DELETE", "PERM_UPDATE", "PERM_READ"})
    public void testRegisterUser() throws Exception {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setUsername("testuser");
        request.setPassword("testpass");
        request.setFirstname("Test");
        request.setLastname("User");
        request.setEmail("test@example.com");

        User createdUser = new User();
        createdUser.setId(1L);
        createdUser.setUsername("testuser");
        createdUser.setPassword("testpass");
        createdUser.setStatus(false);

        Mockito.when(userService.registerUser(any(UserRegistrationRequest.class)))
                .thenReturn(createdUser);

        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    @WithMockUser(username = "testuser", authorities = {"PERM_DELETE"})
    public void testDeleteUserSuccess() throws Exception {
        mockMvc.perform(delete("/user")
                        .param("userId", "1"))
                .andExpect(status().isOk());
        Mockito.verify(userService).deleteUser(1L);
    }

    @Test
    @WithMockUser(username = "testuser", authorities = {"PERM_DELETE"})
    public void testDeleteUserNotFound() throws Exception {
        doThrow(new UserNotFoundException("User not found")).when(userService).deleteUser(2L);
        mockMvc.perform(delete("/user")
                        .param("userId", "2"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("User not found"));
    }

    @Test
    @WithMockUser(username = "testuser", authorities = {"PERM_UPDATE"})
    public void testUpdateUserStatus() throws Exception {
        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setUsername("testuser");
        updatedUser.setStatus(true);

        Mockito.when(userService.updateUserStatus(1L, true))
                .thenReturn(updatedUser);

        mockMvc.perform(patch("/user/1/status")
                        .param("activate", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true));
    }

    @Test
    @WithMockUser(username = "testuser", authorities = {"PERM_READ"})
    public void testListAllUsers() throws Exception {
        // Create anonymous implementations of UserProjection
        UserProjection user1 = new UserProjection() {
            @Override
            public Long getId() {
                return 1L;
            }
            @Override
            public String getUsername() {
                return "user1";
            }
        };
        UserProjection user2 = new UserProjection() {
            @Override
            public Long getId() {
                return 2L;
            }
            @Override
            public String getUsername() {
                return "user2";
            }
        };

        Mockito.when(userService.getAllUsers()).thenReturn(Arrays.asList(user1, user2));

        mockMvc.perform(get("/user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].username").value("user1"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].username").value("user2"));
    }

    @Test
    @WithMockUser(username = "testuser", authorities = {"PERM_READ"})
    public void testGetUserInfo() throws Exception {
        UserInfoResponse infoResponse = new UserInfoResponse(1L, "user1", "First", "Last", "user1@example.com");

        Mockito.when(userService.getUserInfo(1L)).thenReturn(infoResponse);

        mockMvc.perform(get("/user/info/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.username").value("user1"))
                .andExpect(jsonPath("$.firstname").value("First"))
                .andExpect(jsonPath("$.lastname").value("Last"))
                .andExpect(jsonPath("$.email").value("user1@example.com"));
    }
}
