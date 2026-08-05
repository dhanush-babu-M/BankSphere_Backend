-- V1__init_schema.sql
-- MySQL 8.0 compatible schema

CREATE TABLE roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    description VARCHAR(255)
);

CREATE TABLE privileges (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    description VARCHAR(255)
);

CREATE TABLE roles_privileges (
    role_id BIGINT NOT NULL,
    privilege_id BIGINT NOT NULL,
    PRIMARY KEY (role_id, privilege_id),
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
    FOREIGN KEY (privilege_id) REFERENCES privileges(id) ON DELETE CASCADE
);

CREATE TABLE users (
    id CHAR(36) PRIMARY KEY,
    username VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    phone_number VARCHAR(20),
    status VARCHAR(20) CHECK (status IN ('ACTIVE', 'INACTIVE', 'LOCKED')) DEFAULT 'ACTIVE',
    enabled BOOLEAN DEFAULT TRUE,
    account_non_locked BOOLEAN DEFAULT TRUE,
    account_non_expired BOOLEAN DEFAULT TRUE,
    credentials_non_expired BOOLEAN DEFAULT TRUE,
    failed_login_attempts INT DEFAULT 0,
    last_login_at DATETIME,
    last_password_changed_at DATETIME,
    mfa_enabled BOOLEAN DEFAULT FALSE,
    mfa_secret VARCHAR(255),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE users_roles (
    user_id CHAR(36) NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

CREATE TABLE refresh_tokens (
    id CHAR(36) PRIMARY KEY,
    token VARCHAR(512) UNIQUE NOT NULL,
    user_id CHAR(36) NOT NULL,
    expiry_date DATETIME NOT NULL,
    revoked BOOLEAN DEFAULT FALSE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE password_reset_tokens (
    id CHAR(36) PRIMARY KEY,
    token_hash VARCHAR(255) UNIQUE NOT NULL,
    user_id CHAR(36) NOT NULL,
    expiry_date DATETIME NOT NULL,
    used BOOLEAN DEFAULT FALSE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE customers (
    id CHAR(36) PRIMARY KEY,
    user_id CHAR(36) UNIQUE NOT NULL,
    customer_number VARCHAR(50) UNIQUE NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE customer_profiles (
    id CHAR(36) PRIMARY KEY,
    customer_id CHAR(36) UNIQUE NOT NULL,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    date_of_birth DATE,
    address TEXT,
    phone_number VARCHAR(20),
    FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE
);

CREATE TABLE kyc_documents (
    id CHAR(36) PRIMARY KEY,
    customer_id CHAR(36) NOT NULL,
    document_type VARCHAR(50),
    document_url VARCHAR(255),
    verification_status VARCHAR(20) CHECK (verification_status IN ('PENDING', 'VERIFIED', 'REJECTED')) DEFAULT 'PENDING',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE
);

CREATE TABLE employees (
    id CHAR(36) PRIMARY KEY,
    user_id CHAR(36) UNIQUE NOT NULL,
    employee_code VARCHAR(50) UNIQUE NOT NULL,
    department VARCHAR(100),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE branches (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    branch_code VARCHAR(20) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    address TEXT,
    contact_number VARCHAR(20)
);

CREATE TABLE accounts (
    id CHAR(36) PRIMARY KEY,
    customer_id CHAR(36) NOT NULL,
    branch_id BIGINT NOT NULL,
    account_number VARCHAR(20) UNIQUE NOT NULL,
    account_type VARCHAR(20) CHECK (account_type IN ('SAVINGS', 'CURRENT', 'SALARY')),
    balance DECIMAL(15, 2) DEFAULT 0.00,
    available_balance DECIMAL(15, 2) DEFAULT 0.00,
    currency VARCHAR(10) DEFAULT 'INR',
    status VARCHAR(20) CHECK (status IN ('ACTIVE', 'DORMANT', 'CLOSED')) DEFAULT 'ACTIVE',
    last_transaction_date DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(id),
    FOREIGN KEY (branch_id) REFERENCES branches(id)
);

CREATE TABLE account_balance_history (
    id CHAR(36) PRIMARY KEY,
    account_id CHAR(36) NOT NULL,
    balance DECIMAL(15, 2) NOT NULL,
    recorded_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE
);

CREATE TABLE account_holds (
    id CHAR(36) PRIMARY KEY,
    account_id CHAR(36) NOT NULL,
    amount DECIMAL(15, 2) NOT NULL,
    reason TEXT,
    status VARCHAR(20) CHECK (status IN ('ACTIVE', 'RELEASED')) DEFAULT 'ACTIVE',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE
);

CREATE TABLE transactions (
    id CHAR(36) PRIMARY KEY,
    account_id CHAR(36) NOT NULL,
    linked_account_id CHAR(36),
    amount DECIMAL(15, 2) NOT NULL,
    currency VARCHAR(10) DEFAULT 'INR',
    transaction_type VARCHAR(30) CHECK (transaction_type IN (
        'DEPOSIT', 'WITHDRAWAL', 'TRANSFER_IN', 'TRANSFER_OUT',
        'PAYMENT', 'WIRE_TRANSFER', 'CREDIT', 'DEBIT'
    )),
    status VARCHAR(20) CHECK (status IN ('PENDING', 'COMPLETED', 'FAILED', 'REVERSED')) DEFAULT 'PENDING',
    reference_number VARCHAR(100) UNIQUE NOT NULL,
    description TEXT,
    narration TEXT,
    balance_before DECIMAL(15, 2),
    balance_after DECIMAL(15, 2),
    channel VARCHAR(50),
    payment_mode VARCHAR(50),
    beneficiary_name VARCHAR(100),
    beneficiary_account_number VARCHAR(30),
    ifsc_code VARCHAR(20),
    value_date DATE,
    settlement_date DATE,
    initiated_by VARCHAR(100),
    approved_by VARCHAR(100),
    reversal_reason TEXT,
    reversed_at DATETIME,
    transaction_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (account_id) REFERENCES accounts(id)
);

CREATE TABLE ledger_entries (
    id CHAR(36) PRIMARY KEY,
    transaction_id CHAR(36) NOT NULL,
    account_id CHAR(36) NOT NULL,
    entry_type VARCHAR(10) CHECK (entry_type IN ('DEBIT', 'CREDIT')),
    amount DECIMAL(15, 2) NOT NULL,
    running_balance DECIMAL(15, 2),
    credit DECIMAL(15, 2) DEFAULT 0.00,
    debit DECIMAL(15, 2) DEFAULT 0.00,
    value_date DATE,
    description TEXT,
    reference_number VARCHAR(100),
    gl_account_code VARCHAR(20),
    entry_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (transaction_id) REFERENCES transactions(id),
    FOREIGN KEY (account_id) REFERENCES accounts(id)
);

CREATE TABLE beneficiaries (
    id CHAR(36) PRIMARY KEY,
    customer_id CHAR(36) NOT NULL,
    beneficiary_account_number VARCHAR(20) NOT NULL,
    beneficiary_name VARCHAR(100) NOT NULL,
    bank_name VARCHAR(100),
    ifsc_code VARCHAR(20),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE
);

CREATE TABLE loans (
    id CHAR(36) PRIMARY KEY,
    customer_id CHAR(36) NOT NULL,
    loan_type VARCHAR(50),
    principal_amount DECIMAL(15, 2) NOT NULL,
    interest_rate DECIMAL(5, 2) NOT NULL,
    tenure_months INT NOT NULL,
    status VARCHAR(20) CHECK (status IN ('ACTIVE', 'CLOSED', 'DEFAULTED')),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(id)
);

CREATE TABLE loan_applications (
    id CHAR(36) PRIMARY KEY,
    customer_id CHAR(36) NOT NULL,
    requested_amount DECIMAL(15, 2) NOT NULL,
    status VARCHAR(20) CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED')) DEFAULT 'PENDING',
    applied_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(id)
);

CREATE TABLE loan_schedules (
    id CHAR(36) PRIMARY KEY,
    loan_id CHAR(36) NOT NULL,
    due_date DATE NOT NULL,
    installment_amount DECIMAL(15, 2) NOT NULL,
    principal_component DECIMAL(15, 2),
    interest_component DECIMAL(15, 2),
    status VARCHAR(20) CHECK (status IN ('PENDING', 'PAID', 'OVERDUE')) DEFAULT 'PENDING',
    FOREIGN KEY (loan_id) REFERENCES loans(id) ON DELETE CASCADE
);

CREATE TABLE loan_collaterals (
    id CHAR(36) PRIMARY KEY,
    loan_id CHAR(36) NOT NULL,
    collateral_type VARCHAR(50),
    value DECIMAL(15, 2),
    description TEXT,
    FOREIGN KEY (loan_id) REFERENCES loans(id) ON DELETE CASCADE
);

CREATE TABLE fixed_deposits (
    id CHAR(36) PRIMARY KEY,
    customer_id CHAR(36) NOT NULL,
    principal_amount DECIMAL(15, 2) NOT NULL,
    interest_rate DECIMAL(5, 2) NOT NULL,
    maturity_date DATE NOT NULL,
    status VARCHAR(20) CHECK (status IN ('ACTIVE', 'MATURED', 'WITHDRAWN')) DEFAULT 'ACTIVE',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(id)
);

CREATE TABLE debit_cards (
    id CHAR(36) PRIMARY KEY,
    account_id CHAR(36) NOT NULL,
    card_number VARCHAR(16) UNIQUE NOT NULL,
    expiry_date DATE NOT NULL,
    cvv VARCHAR(3) NOT NULL,
    status VARCHAR(20) CHECK (status IN ('ACTIVE', 'BLOCKED', 'EXPIRED')) DEFAULT 'ACTIVE',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (account_id) REFERENCES accounts(id)
);

CREATE TABLE credit_cards (
    id CHAR(36) PRIMARY KEY,
    customer_id CHAR(36) NOT NULL,
    card_number VARCHAR(16) UNIQUE NOT NULL,
    credit_limit DECIMAL(15, 2) NOT NULL,
    available_credit DECIMAL(15, 2) NOT NULL,
    expiry_date DATE NOT NULL,
    cvv VARCHAR(3) NOT NULL,
    status VARCHAR(20) CHECK (status IN ('ACTIVE', 'BLOCKED', 'EXPIRED')) DEFAULT 'ACTIVE',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(id)
);

CREATE TABLE credit_card_transactions (
    id CHAR(36) PRIMARY KEY,
    credit_card_id CHAR(36) NOT NULL,
    amount DECIMAL(15, 2) NOT NULL,
    merchant_name VARCHAR(100),
    transaction_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) CHECK (status IN ('PENDING', 'COMPLETED', 'FAILED')),
    FOREIGN KEY (credit_card_id) REFERENCES credit_cards(id)
);

CREATE TABLE credit_card_bills (
    id CHAR(36) PRIMARY KEY,
    credit_card_id CHAR(36) NOT NULL,
    billing_cycle_start DATE,
    billing_cycle_end DATE,
    total_amount_due DECIMAL(15, 2) NOT NULL,
    minimum_amount_due DECIMAL(15, 2) NOT NULL,
    due_date DATE NOT NULL,
    status VARCHAR(20) CHECK (status IN ('UNPAID', 'PARTIAL', 'PAID')) DEFAULT 'UNPAID',
    FOREIGN KEY (credit_card_id) REFERENCES credit_cards(id)
);

CREATE TABLE bill_merchants (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    category VARCHAR(50),
    status VARCHAR(20) DEFAULT 'ACTIVE'
);

CREATE TABLE payment_gateway_logs (
    id CHAR(36) PRIMARY KEY,
    transaction_id CHAR(36) NOT NULL,
    gateway_name VARCHAR(50),
    gateway_reference VARCHAR(100),
    request_payload TEXT,
    response_payload TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (transaction_id) REFERENCES transactions(id)
);

CREATE TABLE audit_logs (
    id CHAR(36) PRIMARY KEY,
    entity_name VARCHAR(100),
    entity_id VARCHAR(100),
    action VARCHAR(50),
    performed_by VARCHAR(100),
    old_value TEXT,
    new_value TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE security_audit_logs (
    id CHAR(36) PRIMARY KEY,
    username VARCHAR(100),
    action VARCHAR(50),
    ip_address VARCHAR(50),
    status VARCHAR(20),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE system_configurations (
    config_key VARCHAR(100) PRIMARY KEY,
    config_value VARCHAR(255) NOT NULL,
    description TEXT,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE pending_operations (
    id CHAR(36) PRIMARY KEY,
    operation_type VARCHAR(50),
    payload TEXT,
    status VARCHAR(20) CHECK (status IN ('PENDING', 'PROCESSING', 'COMPLETED', 'FAILED')) DEFAULT 'PENDING',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
