package com.example.auth.adapters.in.web.converter;

import com.example.auth.application.dto.LoginDTO;
import com.example.auth.application.dto.TokenValidationDTO;
import com.example.auth.adapters.in.web.dto.LoginResponse;
import com.example.auth.adapters.in.web.dto.ValidateResponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthResponseConverter {
    private final ModelMapper modelMapper;

    public LoginResponse toLoginResponse(LoginDTO result) {
        return modelMapper.map(result, LoginResponse.class);
    }

    public ValidateResponse toValidateResponse(TokenValidationDTO result) {
        return modelMapper.map(result, ValidateResponse.class);
    }
}