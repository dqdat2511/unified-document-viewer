CREATE TABLE IF NOT EXISTS active_vins (
    id     BIGINT       NOT NULL AUTO_INCREMENT,
    vin    VARCHAR(17)  NOT NULL,
    status VARCHAR(16)  NOT NULL DEFAULT 'ACTIVE',
    PRIMARY KEY (id),
    UNIQUE INDEX uq_active_vins_vin (vin),
    INDEX idx_active_vins_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
