package com.example.auth.adapters.in.web.converter;

import com.example.auth.adapters.in.web.dto.LoginRequest;
import com.example.auth.application.command.LoginCommand;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthRequestConverter {
    private final ModelMapper modelMapper;

    public LoginCommand toCommand(LoginRequest request) {
        return modelMapper.map(request, LoginCommand.class);
    }
}
