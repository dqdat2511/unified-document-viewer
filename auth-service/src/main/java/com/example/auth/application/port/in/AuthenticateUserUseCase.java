package com.example.auth.application.port.in;

import com.example.auth.application.command.LoginCommand;
import com.example.auth.application.dto.LoginDTO;
import com.example.auth.application.dto.TokenValidationDTO;
import java.util.Optional;

public interface AuthenticateUserUseCase {
    Optional<LoginDTO> login(LoginCommand command);

    TokenValidationDTO validateAuthorizationHeader(String authorizationHeader);
}
