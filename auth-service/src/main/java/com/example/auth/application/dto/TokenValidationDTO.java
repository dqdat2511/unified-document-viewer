package com.example.auth.application.dto;

public class TokenValidationDTO {

    private final boolean valid;
    private final String username;
    private final String role;

    public TokenValidationDTO(boolean valid, String username, String role) {
        this.valid = valid;
        this.username = username;
        this.role = role;
    }

    public boolean isValid() { return valid; }
    public String getUsername() { return username; }
    public String getRole() { return role; }
}
