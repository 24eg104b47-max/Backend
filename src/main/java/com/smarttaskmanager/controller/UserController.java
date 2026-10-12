package com.smarttaskmanager.controller;

import com.smarttaskmanager.dto.*;
import com.smarttaskmanager.entity.User;
import com.smarttaskmanager.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class UserController {
    private final AuthService authService;

    public UserController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/me")
    public Map<String, Object> me(Authentication auth) {
        User u = authService.me(auth.getName());
        return Map.of(
                "id", u.getId(),
                "username", u.getUsername(),
                "email", u.getEmail(),
                "createdAt", u.getCreatedAt() == null ? "" : u.getCreatedAt().toString());
    }

    @PutMapping("/me")
    public AuthResponse updateProfile(Authentication auth, @Valid @RequestBody UpdateProfileRequest req) {
        return authService.updateProfile(auth.getName(), req);
    }

    @PutMapping("/me/password")
    public Map<String, String> changePassword(Authentication auth, @Valid @RequestBody ChangePasswordRequest req) {
        authService.changePassword(auth.getName(), req);
        return Map.of("message", "Password changed successfully");
    }
}
