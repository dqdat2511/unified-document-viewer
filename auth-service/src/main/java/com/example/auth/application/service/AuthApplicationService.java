package com.example.auth.application.service;

import com.example.auth.application.command.LoginCommand;
import com.example.auth.application.command.RegisterCommand;
import com.example.auth.application.port.in.AuthenticateUserUseCase;
import com.example.auth.application.port.in.RegisterUserUseCase;
import com.example.auth.application.port.out.TokenPort;
import com.example.auth.application.port.out.UserAccountPort;
import com.example.auth.application.dto.LoginDTO;
import com.example.auth.application.dto.TokenValidationDTO;
import com.example.auth.domain.exception.AuthErrorCode;
import com.example.auth.domain.exception.AuthRuntimeException;
import com.example.auth.domain.model.AuthenticatedUser;
import com.example.auth.domain.model.UserAccount;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class AuthApplicationService implements AuthenticateUserUseCase, RegisterUserUseCase {

    private final UserAccountPort userAccountPort;
    private final TokenPort tokenPort;
    private final PasswordEncoder passwordEncoder;  // injected BCryptPasswordEncoder

    @Override
    public Optional<LoginDTO> login(LoginCommand command) {
        return userAccountPort.findByUsername(command.getUsername())
            .filter(user -> passwordEncoder.matches(command.getPassword(), user.getPassword()))
            .map(user -> new AuthenticatedUser(user.getUsername(), user.getRole()))
            .map(authenticatedUser -> new LoginDTO(
                tokenPort.generate(authenticatedUser),
                "Bearer",
                tokenPort.getExpirationSeconds(),
                authenticatedUser.getRole()
            ));
    }

    @Override
    public TokenValidationDTO validateAuthorizationHeader(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return new TokenValidationDTO(false, null, null);
        }
        return tokenPort.validate(authorizationHeader.substring(7));
    }

    @Override
    public void register(RegisterCommand command) {
        try {
            UserAccount account = new UserAccount();
            account.setUsername(command.username());
            account.setPassword(passwordEncoder.encode(command.password()));
            account.setRole("USER");
            userAccountPort.save(account);
        } catch (DataIntegrityViolationException ex) {
            throw new AuthRuntimeException(AuthErrorCode.USERNAME_TAKEN, "Username is already taken");
        }
    }
}
