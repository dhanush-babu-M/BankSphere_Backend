-- V2__seed_roles_and_admin.sql

INSERT INTO roles (name, description) VALUES 
('ROLE_ADMIN',    'System Administrator with full access'),
('ROLE_EMPLOYEE', 'Bank Employee with operational access'),
('ROLE_CUSTOMER', 'Bank Customer with self-service access'),
('ROLE_AUDITOR',  'Auditor with read-only access');

INSERT INTO privileges (name, description) VALUES 
('READ_ACCOUNT',       'View account details'),
('WRITE_ACCOUNT',      'Create and update accounts'),
('READ_TRANSACTION',   'View transaction history'),
('WRITE_TRANSACTION',  'Initiate transactions'),
('APPROVE_LOAN',       'Approve loan applications');

-- Assumes IDs 1,2,3,4 for roles and 1,2,3,4,5 for privileges
INSERT INTO roles_privileges (role_id, privilege_id) VALUES 
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5),  -- ADMIN: all
(2, 1), (2, 3), (2, 5),                   -- EMPLOYEE: read acct, read txn, approve loan
(3, 1), (3, 3),                            -- CUSTOMER: read acct, read txn
(4, 1), (4, 3);                            -- AUDITOR: read acct, read txn

-- BCrypt hash of 'Admin@123!' generated with cost 10
-- You can verify at: https://bcrypt-generator.com/
-- Hash: $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy
INSERT INTO users (id, username, password, email, status, enabled, account_non_locked, account_non_expired, credentials_non_expired, failed_login_attempts, mfa_enabled) 
VALUES (
    '00000000-0000-0000-0000-000000000001',
    'admin',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
    'admin@banksphere.com',
    'ACTIVE',
    TRUE,
    TRUE,
    TRUE,
    TRUE,
    0,
    FALSE
);

INSERT INTO users_roles (user_id, role_id) 
VALUES ('00000000-0000-0000-0000-000000000001', 1);

INSERT INTO system_configurations (config_key, config_value, description) VALUES
('INTEREST_RATE_SAVINGS',    '4.00',      'Annual interest rate for savings accounts'),
('TRANSACTION_LIMIT_DAILY',  '100000.00', 'Maximum allowed daily transaction limit in INR'),
('LOAN_APPROVAL_THRESHOLD',  '500000.00', 'Threshold amount for auto loan approval in INR'),
('OTP_EXPIRY_MINUTES',       '5',         'OTP expiry time in minutes'),
('MAX_LOGIN_ATTEMPTS',       '5',         'Max failed login attempts before account lock');

INSERT INTO bill_merchants (name, category, status) VALUES
('Electricity Board',   'UTILITIES', 'ACTIVE'),
('Water Supply Dept',   'UTILITIES', 'ACTIVE'),
('City Gas Services',   'UTILITIES', 'ACTIVE'),
('Telecom Operator',    'TELECOM',   'ACTIVE'),
('Cable TV Services',   'TELECOM',   'ACTIVE'),
('Insurance Premium',   'INSURANCE', 'ACTIVE');

INSERT INTO branches (branch_code, name, address, contact_number) VALUES
('BSP001', 'Main Branch',        'No. 1, MG Road, Bangalore - 560001',           '+91-80-12345678'),
('BSP002', 'Hyderabad Branch',   'No. 45, Banjara Hills, Hyderabad - 500034',    '+91-40-87654321'),
('BSP003', 'Chennai Branch',     'No. 10, Anna Salai, Chennai - 600002',         '+91-44-11223344');
