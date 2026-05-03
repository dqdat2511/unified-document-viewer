package com.example.auth.application.port.out;

import com.example.auth.application.dto.TokenValidationDTO;
import com.example.auth.domain.model.AuthenticatedUser;

public interface TokenPort {
    String generate(AuthenticatedUser authenticatedUser);

    TokenValidationDTO validate(String token);

    long getExpirationSeconds();
}
