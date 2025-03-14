package com.example.demo.controller;

import com.example.demo.dto.UserRegistrationRequest;
import com.example.demo.dto.UserInfoResponse;
import com.example.demo.entity.User;
import com.example.demo.projection.UserProjection;
import com.example.demo.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
//import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

@RestController
@RequestMapping({"/user", "/user/"})
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // User Registration - POST /user
    @PostMapping
    @PreAuthorize("hasAnyAuthority('PERM_WRITE')")
    public ResponseEntity<User> registerUser(@RequestBody UserRegistrationRequest request) {
        User createdUser = userService.registerUser(request);
        return ResponseEntity.ok(createdUser);
    }

    @DeleteMapping
    @PreAuthorize("hasAuthority('PERM_DELETE')")
    public ResponseEntity<Void> deleteUser(@RequestParam Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok().build();
    }

    // Activate/Deactivate a User - PATCH /user/{userId}/status?activate={activate}
    @PatchMapping("/{userId}/status")
    @PreAuthorize("hasAuthority('PERM_UPDATE')")
    public ResponseEntity<User> updateUserStatus(@PathVariable Long userId,
                                                 @RequestParam boolean activate) {
        User updatedUser = userService.updateUserStatus(userId, activate);
        return ResponseEntity.ok(updatedUser);
    }

    // List all Users - GET /user
    // Using Spring Data Projection to return only id and username.
    @GetMapping
    @PreAuthorize("hasAuthority('PERM_READ')")
    public ResponseEntity<List<UserProjection>> listAllUsers() {
        List<UserProjection> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    // Get User By user Id - GET /user/info/{userId}
    @GetMapping("/info/{userId}")
    public ResponseEntity<UserInfoResponse> getUserInfo(@PathVariable Long userId) {
        UserInfoResponse userInfo = userService.getUserInfo(userId);
        return ResponseEntity.ok(userInfo);
    }
}
