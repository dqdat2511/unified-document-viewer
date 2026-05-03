package com.example.document.application.port.out;

import com.example.document.domain.ActiveVin;

import java.util.Optional;

/**
 * Outbound Port — Active VIN Repository (MySQL)
 * DB is the source of truth for active VIN registry only.
 */
public interface DocumentRepositoryPort {

    Optional<ActiveVin> findByVin(String vin);
}
