-- V2__seed_roles_and_admin.sql

INSERT INTO roles (name) VALUES 
('ROLE_ADMIN'), 
('ROLE_EMPLOYEE'), 
('ROLE_CUSTOMER'), 
('ROLE_AUDITOR');

INSERT INTO privileges (name) VALUES 
('READ_ACCOUNT'), 
('WRITE_ACCOUNT'), 
('READ_TRANSACTION'), 
('WRITE_TRANSACTION'), 
('APPROVE_LOAN');

-- Assumes IDs 1, 2, 3, 4 for roles and 1, 2, 3, 4, 5 for privileges respectively
INSERT INTO role_privileges (role_id, privilege_id) VALUES 
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5), -- Admin
(2, 1), (2, 3), (2, 5), -- Employee
(3, 1), (3, 3), -- Customer
(4, 1), (4, 3); -- Auditor

-- BCrypt hash of 'Admin@123!' -> $2a$10$w8.1Clye7t0/sO9f9k.mue5z8K6Y6Q4qV2.G4n7nZ4JtD9Kx6Gk0W
INSERT INTO users (id, username, password, email, status) 
VALUES ('00000000-0000-0000-0000-000000000000', 'admin', '$2a$10$w8.1Clye7t0/sO9f9k.mue5z8K6Y6Q4qV2.G4n7nZ4JtD9Kx6Gk0W', 'admin@banksphere.com', 'ACTIVE');

INSERT INTO user_roles (user_id, role_id) 
VALUES ('00000000-0000-0000-0000-000000000000', 1);

INSERT INTO system_configurations (config_key, config_value, description) VALUES
('INTEREST_RATE_SAVINGS', '4.00', 'Annual interest rate for savings accounts'),
('TRANSACTION_LIMIT_DAILY', '100000.00', 'Maximum allowed daily transaction limit'),
('LOAN_APPROVAL_THRESHOLD', '500000.00', 'Threshold amount for auto loan approval');

INSERT INTO bill_merchants (name, category, status) VALUES
('Electricity Board', 'UTILITIES', 'ACTIVE'),
('Water Supply Dept', 'UTILITIES', 'ACTIVE'),
('City Gas Services', 'UTILITIES', 'ACTIVE'),
('Telecom Operator', 'TELECOM', 'ACTIVE');
