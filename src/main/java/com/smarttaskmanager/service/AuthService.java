package com.smarttaskmanager.service;

import com.smarttaskmanager.config.JwtUtil;
import com.smarttaskmanager.dto.*;
import com.smarttaskmanager.entity.User;
import com.smarttaskmanager.repository.UserRepository;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                      AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    public AuthResponse register(RegisterRequest req) {
        if (userRepository.existsByUsername(req.getUsername()))
            throw new IllegalArgumentException("Username already taken");
        if (userRepository.existsByEmail(req.getEmail()))
            throw new IllegalArgumentException("Email already in use");
        User u = new User();
        u.setUsername(req.getUsername());
        u.setEmail(req.getEmail());
        u.setPassword(passwordEncoder.encode(req.getPassword()));
        userRepository.save(u);
        String token = jwtUtil.generateToken(u.getUsername());
        return new AuthResponse(token, u.getId(), u.getUsername(), u.getEmail());
    }

    public AuthResponse login(LoginRequest req) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getUsernameOrEmail(), req.getPassword()));
        String principal = auth.getName();
        var user = userRepository.findByUsername(principal)
                .or(() -> userRepository.findByEmail(principal)).orElseThrow();
        String token = jwtUtil.generateToken(user.getUsername());
        return new AuthResponse(token, user.getId(), user.getUsername(), user.getEmail());
    }

    public Long userIdOf(String username) {
        var user = userRepository.findByUsername(username)
                .or(() -> userRepository.findByEmail(username)).orElseThrow();
        return user.getId();
    }

    public User me(String username) {
        return userRepository.findByUsername(username)
                .or(() -> userRepository.findByEmail(username))
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    /** Returns a fresh token when the username changed (it is the JWT subject). */
    public AuthResponse updateProfile(String username, UpdateProfileRequest req) {
        User u = me(username);
        if (!u.getUsername().equals(req.getUsername()) && userRepository.existsByUsername(req.getUsername()))
            throw new IllegalArgumentException("Username already taken");
        if (!u.getEmail().equals(req.getEmail()) && userRepository.existsByEmail(req.getEmail()))
            throw new IllegalArgumentException("Email already in use");
        u.setUsername(req.getUsername());
        u.setEmail(req.getEmail());
        userRepository.save(u);
        return new AuthResponse(jwtUtil.generateToken(u.getUsername()), u.getId(), u.getUsername(), u.getEmail());
    }

    public void changePassword(String username, ChangePasswordRequest req) {
        User u = me(username);
        if (!passwordEncoder.matches(req.getCurrentPassword(), u.getPassword()))
            throw new IllegalArgumentException("Current password is incorrect");
        u.setPassword(passwordEncoder.encode(req.getNewPassword()));
        userRepository.save(u);
    }
}
