package com.example.auth.adapters.in.web.dto;

public class ValidateResponse {

    private boolean valid;
    private String username;
    private String role;

    public ValidateResponse() {}

    public ValidateResponse(boolean valid, String username, String role) {
        this.valid = valid;
        this.username = username;
        this.role = role;
    }

    public boolean isValid() { return valid; }
    public String getUsername() { return username; }
    public String getRole() { return role; }
    public void setValid(boolean valid) { this.valid = valid; }
    public void setUsername(String username) { this.username = username; }
    public void setRole(String role) { this.role = role; }
}
