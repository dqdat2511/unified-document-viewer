package com.example.auth.adapters.in.web.dto;

public class LoginResponse {

    private String token;
    private String tokenType;
    private long expiresInSeconds;
    private String role;

    public LoginResponse() {}

    public LoginResponse(String token, String tokenType, long expiresInSeconds, String role) {
        this.token = token;
        this.tokenType = tokenType;
        this.expiresInSeconds = expiresInSeconds;
        this.role = role;
    }

    public String getToken() { return token; }
    public String getTokenType() { return tokenType; }
    public long getExpiresInSeconds() { return expiresInSeconds; }
    public String getRole() { return role; }
    public void setToken(String token) { this.token = token; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }
    public void setExpiresInSeconds(long expiresInSeconds) { this.expiresInSeconds = expiresInSeconds; }
    public void setRole(String role) { this.role = role; }
}
