package com.example.auth.adapters.out.persistence;

import com.example.auth.adapters.out.persistence.converter.UserAccountEntityConverter;
import com.example.auth.adapters.out.persistence.repository.UserAccountJpaRepository;
import com.example.auth.application.port.out.UserAccountPort;
import com.example.auth.domain.model.UserAccount;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JpaUserAccountAdapter implements UserAccountPort {

    private final UserAccountJpaRepository userAccountJpaRepository;
    private final UserAccountEntityConverter userAccountEntityConverter;

    @Override
    public Optional<UserAccount> findByUsername(String username) {
        return userAccountJpaRepository.findById(username)
            .map(userAccountEntityConverter::toDomain);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userAccountJpaRepository.existsById(username);
    }

    @Override
    public void save(UserAccount account) {
        userAccountJpaRepository.save(userAccountEntityConverter.toEntity(account));
    }
}