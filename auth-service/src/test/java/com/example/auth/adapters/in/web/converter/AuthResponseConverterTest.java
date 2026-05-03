package com.example.auth.adapters.in.web.converter;

import com.example.auth.adapters.in.web.dto.LoginResponse;
import com.example.auth.adapters.in.web.dto.ValidateResponse;
import com.example.auth.application.dto.LoginDTO;
import com.example.auth.application.dto.TokenValidationDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("AuthResponseConverter — Unit Tests")
class AuthResponseConverterTest {

    private AuthResponseConverter sut;

    @BeforeEach
    void setUp() {
        sut = new AuthResponseConverter(new ModelMapper());
    }

    @Test
    @DisplayName("toLoginResponse — should map token from LoginDTO")
    void shouldMapToken() {
        LoginDTO dto = new LoginDTO("jwt.token.here", "Bearer", 3600L, "USER");

        LoginResponse response = sut.toLoginResponse(dto);

        assertThat(response.getToken()).isEqualTo("jwt.token.here");
    }

    @Test
    @DisplayName("toLoginResponse — should map all fields from LoginDTO")
    void shouldMapAllLoginFields() {
        LoginDTO dto = new LoginDTO("tok", "Bearer", 7200L, "ADMIN");

        LoginResponse response = sut.toLoginResponse(dto);

        assertThat(response.getToken()).isEqualTo("tok");
        assertThat(response.getTokenType()).isEqualTo("Bearer");
        assertThat(response.getExpiresInSeconds()).isEqualTo(7200L);
        assertThat(response.getRole()).isEqualTo("ADMIN");
    }

    @Test
    @DisplayName("toValidateResponse — should map valid=true for a valid token")
    void shouldMapValidTokenValidation() {
        TokenValidationDTO dto = new TokenValidationDTO(true, "user@example.com", "USER");

        ValidateResponse response = sut.toValidateResponse(dto);

        assertThat(response.isValid()).isTrue();
        assertThat(response.getUsername()).isEqualTo("user@example.com");
        assertThat(response.getRole()).isEqualTo("USER");
    }

    @Test
    @DisplayName("toValidateResponse — should map valid=false for an invalid token")
    void shouldMapInvalidTokenValidation() {
        TokenValidationDTO dto = new TokenValidationDTO(false, null, null);

        ValidateResponse response = sut.toValidateResponse(dto);

        assertThat(response.isValid()).isFalse();
    }
}
