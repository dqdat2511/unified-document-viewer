package com.example.auth.application.port.in;

import com.example.auth.application.command.RegisterCommand;

public interface RegisterUserUseCase {
    void register(RegisterCommand command);
}
