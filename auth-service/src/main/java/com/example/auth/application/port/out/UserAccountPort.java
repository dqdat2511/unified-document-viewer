package com.example.auth.application.port.out;

import com.example.auth.domain.model.UserAccount;
import java.util.Optional;

public interface UserAccountPort {
    Optional<UserAccount> findByUsername(String username);
    boolean existsByUsername(String username);
    void save(UserAccount account);
}
