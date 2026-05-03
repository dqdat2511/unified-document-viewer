package com.example.auth.application;

import com.example.auth.application.command.LoginCommand;
import com.example.auth.application.dto.LoginDTO;
import com.example.auth.application.port.out.TokenPort;
import com.example.auth.application.port.out.UserAccountPort;
import com.example.auth.application.service.AuthApplicationService;
import com.example.auth.domain.model.AuthenticatedUser;
import com.example.auth.domain.model.UserAccount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthApplicationService — Unit Tests")
class AuthApplicationServiceTest {

    @Mock private UserAccountPort userAccountPort;
    @Mock private TokenPort tokenPort;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(4); // low strength for tests
    private AuthApplicationService sut;

    @BeforeEach
    void setUp() {
        sut = new AuthApplicationService(userAccountPort, tokenPort, passwordEncoder);
    }

    @Test
    @DisplayName("Should return JWT on valid credentials")
    void shouldReturnTokenOnValidCredentials() {
        String rawPassword = "admin123";
        String hashed = passwordEncoder.encode(rawPassword);

        UserAccount user = userWithPassword(hashed);
        when(userAccountPort.findByUsername("admin")).thenReturn(Optional.of(user));
        when(tokenPort.generate(any(AuthenticatedUser.class))).thenReturn("mock-jwt");
        when(tokenPort.getExpirationSeconds()).thenReturn(600L);

        Optional<LoginDTO> result = sut.login(new LoginCommand("admin", rawPassword));

        assertThat(result).isPresent();
        assertThat(result.get().getToken()).isEqualTo("mock-jwt");
        assertThat(result.get().getTokenType()).isEqualTo("Bearer");
    }

    @Test
    @DisplayName("Should return empty on wrong password")
    void shouldReturnEmptyOnWrongPassword() {
        String hashed = passwordEncoder.encode("correctPassword");
        when(userAccountPort.findByUsername("admin")).thenReturn(Optional.of(userWithPassword(hashed)));

        Optional<LoginDTO> result = sut.login(new LoginCommand("admin", "wrongPassword"));

        assertThat(result).isEmpty();
        verifyNoInteractions(tokenPort);
    }

    @Test
    @DisplayName("Should return empty on unknown username")
    void shouldReturnEmptyOnUnknownUser() {
        when(userAccountPort.findByUsername("ghost")).thenReturn(Optional.empty());

        Optional<LoginDTO> result = sut.login(new LoginCommand("ghost", "password"));

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should validate valid Bearer token")
    void shouldValidateValidToken() {
        // Delegate entirely to TokenPort — just verify it's called
        sut.validateAuthorizationHeader("Bearer some-token");
        verify(tokenPort).validate("some-token");
    }

    @Test
    @DisplayName("Should return invalid result for malformed Authorization header")
    void shouldReturnInvalidForMalformedHeader() {
        var result = sut.validateAuthorizationHeader("Basic dXNlcjpwYXNz");
        assertThat(result.isValid()).isFalse();
        verifyNoInteractions(tokenPort);
    }

    @Test
    @DisplayName("Should return invalid result for null header")
    void shouldReturnInvalidForNullHeader() {
        var result = sut.validateAuthorizationHeader(null);
        assertThat(result.isValid()).isFalse();
    }

    private UserAccount userWithPassword(String hashedPassword) {
        UserAccount u = new UserAccount();
        u.setUsername("admin");
        u.setPassword(hashedPassword);
        u.setRole("ADMIN");
        return u;
    }
}
