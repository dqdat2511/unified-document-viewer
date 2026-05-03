-- Passwords BCrypt-hashed (strength 10)
-- admin=admin123, viewer=viewer123
INSERT IGNORE INTO auth_users (username, password, role) VALUES
    ('admin',  '$2a$10$dAWu.9ysT13zK5SUb6.Qv.rp8LH491xAeQGNH.IVuYrPN8UssX73e', 'ADMIN'),
    ('viewer', '$2a$10$BudOT4zIomxhBnB8oLl/5uLU9jTJ3wJuFnlo5Yjrf5rWyuW1.Eq2q', 'VIEWER');
