package com.example.auth.adapters.out.persistence.converter;

import com.example.auth.adapters.out.persistence.entity.UserAccountEntity;
import com.example.auth.domain.model.UserAccount;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserAccountEntityConverter {
    private final ModelMapper modelMapper;

    public UserAccount toDomain(UserAccountEntity entity) {
        return modelMapper.map(entity, UserAccount.class);
    }

    public UserAccountEntity toEntity(UserAccount domain) {
        return modelMapper.map(domain, UserAccountEntity.class);
    }
}
