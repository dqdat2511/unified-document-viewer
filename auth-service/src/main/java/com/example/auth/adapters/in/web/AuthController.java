package com.example.auth.adapters.in.web;

import com.example.auth.adapters.in.web.converter.AuthRequestConverter;
import com.example.auth.adapters.in.web.converter.AuthResponseConverter;
import com.example.auth.application.command.LoginCommand;
import com.example.auth.application.command.RegisterCommand;
import com.example.auth.application.port.in.AuthenticateUserUseCase;
import com.example.auth.application.port.in.RegisterUserUseCase;
import com.example.auth.application.dto.LoginDTO;
import com.example.auth.application.dto.TokenValidationDTO;
import com.example.auth.adapters.in.web.dto.LoginRequest;
import com.example.auth.adapters.in.web.dto.LoginResponse;
import com.example.auth.adapters.in.web.dto.RegisterRequest;
import com.example.auth.adapters.in.web.dto.ValidateResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticateUserUseCase authenticateUserUseCase;
    private final RegisterUserUseCase registerUserUseCase;
    private final AuthRequestConverter authRequestConverter;
    private final AuthResponseConverter authResponseConverter;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginCommand command = authRequestConverter.toCommand(request);
        Optional<LoginDTO> loginResult = authenticateUserUseCase.login(command);
        if (loginResult.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(authResponseConverter.toLoginResponse(loginResult.get()));
    }

    @PostMapping("/validate")
    public ResponseEntity<ValidateResponse> validate(@RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        TokenValidationDTO result = authenticateUserUseCase.validateAuthorizationHeader(authorizationHeader);
        ValidateResponse response = authResponseConverter.toValidateResponse(result);
        if (!result.isValid()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@Valid @RequestBody RegisterRequest request) {
        registerUserUseCase.register(new RegisterCommand(request.username(), request.password()));
    }
}
