package com.example.auth.adapters.out.persistence.repository;

import com.example.auth.adapters.out.persistence.entity.UserAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAccountJpaRepository extends JpaRepository<UserAccountEntity, String> {
}