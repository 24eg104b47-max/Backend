package com.smarttaskmanager.dto;

public class AuthResponse {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String username;
    private String email;

    public AuthResponse(String token, Long id, String username, String email) {
        this.token = token; this.id = id; this.username = username; this.email = email;
    }
    public String getToken() { return token; }
    public String getType() { return type; }
    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
}
