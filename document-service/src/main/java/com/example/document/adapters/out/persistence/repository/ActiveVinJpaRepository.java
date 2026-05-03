package com.example.document.adapters.out.persistence.repository;

import com.example.document.adapters.out.persistence.entity.ActiveVinEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActiveVinJpaRepository extends JpaRepository<ActiveVinEntity, Long> {
    Optional<ActiveVinEntity> findByVin(String vin);
}
