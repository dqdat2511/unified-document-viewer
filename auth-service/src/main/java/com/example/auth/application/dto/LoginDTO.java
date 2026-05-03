package com.example.auth.application.dto;

public class LoginDTO {

    private final String token;
    private final String tokenType;
    private final long expiresInSeconds;
    private final String role;

    public LoginDTO(String token, String tokenType, long expiresInSeconds, String role) {
        this.token = token;
        this.tokenType = tokenType;
        this.expiresInSeconds = expiresInSeconds;
        this.role = role;
    }

    public String getToken() { return token; }
    public String getTokenType() { return tokenType; }
    public long getExpiresInSeconds() { return expiresInSeconds; }
    public String getRole() { return role; }
}
