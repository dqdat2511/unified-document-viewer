package com.example.auth.domain.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserAccount {
    private String username;
    private String password;
    private String role;
}
