CREATE TABLE IF NOT EXISTS auth_users (
    username VARCHAR(64)  NOT NULL,
    password VARCHAR(255) NOT NULL,
    role     VARCHAR(32)  NOT NULL,
    PRIMARY KEY (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
