package com.example.auth.adapters.in.web.converter;

import com.example.auth.adapters.in.web.dto.LoginRequest;
import com.example.auth.application.command.LoginCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("AuthRequestConverter — Unit Tests")
class AuthRequestConverterTest {

    private AuthRequestConverter sut;

    @BeforeEach
    void setUp() {
        sut = new AuthRequestConverter(new ModelMapper());
    }

    @Test
    @DisplayName("Should map username from LoginRequest to LoginCommand")
    void shouldMapUsername() {
        LoginRequest request = new LoginRequest("user@example.com", "secret");

        LoginCommand command = sut.toCommand(request);

        assertThat(command.getUsername()).isEqualTo("user@example.com");
    }

    @Test
    @DisplayName("Should map password from LoginRequest to LoginCommand")
    void shouldMapPassword() {
        LoginRequest request = new LoginRequest("user@example.com", "secret");

        LoginCommand command = sut.toCommand(request);

        assertThat(command.getPassword()).isEqualTo("secret");
    }

    @Test
    @DisplayName("Should map all fields in a single call")
    void shouldMapAllFields() {
        LoginRequest request = new LoginRequest("admin", "p@ssw0rd");

        LoginCommand command = sut.toCommand(request);

        assertThat(command.getUsername()).isEqualTo("admin");
        assertThat(command.getPassword()).isEqualTo("p@ssw0rd");
    }
}
