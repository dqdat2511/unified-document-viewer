package com.example.auth.adapters.in.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
    @NotBlank(message = "{error.username.required}")
    @Size(min = 3, max = 64, message = "{error.username.size}")
    @Pattern(regexp = "^[a-zA-Z0-9_.-]+$", message = "{error.username.pattern}")
    String username,

    @NotBlank(message = "{error.password.required}")
    @Size(min = 8, max = 128, message = "{error.password.size}")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^a-zA-Z0-9]).+$",
        message = "{error.password.pattern}"
    )
    String password
) {}
