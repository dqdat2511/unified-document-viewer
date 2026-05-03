package com.example.document.adapters.out.persistence;

import com.example.document.adapters.out.persistence.repository.ActiveVinJpaRepository;
import com.example.document.application.port.out.DocumentRepositoryPort;
import com.example.document.domain.ActiveVin;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Outbound Adapter — Active VIN Repository (MySQL via JPA)
 */
@Component
@RequiredArgsConstructor
public class JpaActiveVinRepositoryAdapter implements DocumentRepositoryPort {

    private final ActiveVinJpaRepository activeVinJpaRepository;

    @Override
    public Optional<ActiveVin> findByVin(String vin) {
        return activeVinJpaRepository.findByVin(vin)
            .map(entity -> new ActiveVin(entity.getVin(), entity.getStatus()));
    }
}
