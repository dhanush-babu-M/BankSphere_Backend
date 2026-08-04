-- V1__init_schema.sql

CREATE TABLE roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE privileges (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE role_privileges (
    role_id BIGINT NOT NULL,
    privilege_id BIGINT NOT NULL,
    PRIMARY KEY (role_id, privilege_id),
    FOREIGN KEY (role_id) REFERENCES roles(id),
    FOREIGN KEY (privilege_id) REFERENCES privileges(id)
);

CREATE TABLE users (
    id UUID PRIMARY KEY,
    username VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    status VARCHAR(20) CHECK (status IN ('ACTIVE', 'INACTIVE', 'LOCKED')) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE user_roles (
    user_id UUID NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (role_id) REFERENCES roles(id)
);

CREATE TABLE refresh_tokens (
    id UUID PRIMARY KEY,
    token VARCHAR(255) UNIQUE NOT NULL,
    user_id UUID NOT NULL,
    expiry_date TIMESTAMP NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE customers (
    id UUID PRIMARY KEY,
    user_id UUID UNIQUE NOT NULL,
    customer_number VARCHAR(50) UNIQUE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE customer_profiles (
    id UUID PRIMARY KEY,
    customer_id UUID UNIQUE NOT NULL,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    date_of_birth DATE,
    address TEXT,
    phone_number VARCHAR(20),
    FOREIGN KEY (customer_id) REFERENCES customers(id)
);

CREATE TABLE kyc_documents (
    id UUID PRIMARY KEY,
    customer_id UUID NOT NULL,
    document_type VARCHAR(50),
    document_url VARCHAR(255),
    verification_status VARCHAR(20) CHECK (verification_status IN ('PENDING', 'VERIFIED', 'REJECTED')) DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(id)
);

CREATE TABLE employees (
    id UUID PRIMARY KEY,
    user_id UUID UNIQUE NOT NULL,
    employee_code VARCHAR(50) UNIQUE NOT NULL,
    department VARCHAR(100),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE branches (
    id SERIAL PRIMARY KEY,
    branch_code VARCHAR(20) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    address TEXT,
    contact_number VARCHAR(20)
);

CREATE TABLE accounts (
    id UUID PRIMARY KEY,
    customer_id UUID NOT NULL,
    branch_id INT NOT NULL,
    account_number VARCHAR(20) UNIQUE NOT NULL,
    account_type VARCHAR(20) CHECK (account_type IN ('SAVINGS', 'CURRENT', 'SALARY')),
    balance DECIMAL(15, 2) DEFAULT 0.0,
    status VARCHAR(20) CHECK (status IN ('ACTIVE', 'DORMANT', 'CLOSED')) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(id),
    FOREIGN KEY (branch_id) REFERENCES branches(id)
);

CREATE TABLE account_balance_history (
    id UUID PRIMARY KEY,
    account_id UUID NOT NULL,
    balance DECIMAL(15, 2) NOT NULL,
    recorded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (account_id) REFERENCES accounts(id)
);

CREATE TABLE account_holds (
    id UUID PRIMARY KEY,
    account_id UUID NOT NULL,
    amount DECIMAL(15, 2) NOT NULL,
    reason TEXT,
    status VARCHAR(20) CHECK (status IN ('ACTIVE', 'RELEASED')) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (account_id) REFERENCES accounts(id)
);

CREATE TABLE transactions (
    id UUID PRIMARY KEY,
    account_id UUID NOT NULL,
    amount DECIMAL(15, 2) NOT NULL,
    transaction_type VARCHAR(20) CHECK (transaction_type IN ('DEPOSIT', 'WITHDRAWAL', 'TRANSFER_IN', 'TRANSFER_OUT', 'PAYMENT')),
    status VARCHAR(20) CHECK (status IN ('PENDING', 'COMPLETED', 'FAILED', 'REVERSED')) DEFAULT 'PENDING',
    reference_number VARCHAR(100) UNIQUE NOT NULL,
    transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    description TEXT,
    FOREIGN KEY (account_id) REFERENCES accounts(id)
);

CREATE TABLE ledger_entries (
    id UUID PRIMARY KEY,
    transaction_id UUID NOT NULL,
    account_id UUID NOT NULL,
    credit DECIMAL(15, 2) DEFAULT 0.0,
    debit DECIMAL(15, 2) DEFAULT 0.0,
    entry_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (transaction_id) REFERENCES transactions(id),
    FOREIGN KEY (account_id) REFERENCES accounts(id)
);

CREATE TABLE beneficiaries (
    id UUID PRIMARY KEY,
    customer_id UUID NOT NULL,
    beneficiary_account_number VARCHAR(20) NOT NULL,
    beneficiary_name VARCHAR(100) NOT NULL,
    bank_name VARCHAR(100),
    ifsc_code VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(id)
);

CREATE TABLE loans (
    id UUID PRIMARY KEY,
    customer_id UUID NOT NULL,
    loan_type VARCHAR(50),
    principal_amount DECIMAL(15, 2) NOT NULL,
    interest_rate DECIMAL(5, 2) NOT NULL,
    tenure_months INT NOT NULL,
    status VARCHAR(20) CHECK (status IN ('ACTIVE', 'CLOSED', 'DEFAULTED')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(id)
);

CREATE TABLE loan_applications (
    id UUID PRIMARY KEY,
    customer_id UUID NOT NULL,
    requested_amount DECIMAL(15, 2) NOT NULL,
    status VARCHAR(20) CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED')) DEFAULT 'PENDING',
    applied_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(id)
);

CREATE TABLE loan_schedules (
    id UUID PRIMARY KEY,
    loan_id UUID NOT NULL,
    due_date DATE NOT NULL,
    installment_amount DECIMAL(15, 2) NOT NULL,
    principal_component DECIMAL(15, 2),
    interest_component DECIMAL(15, 2),
    status VARCHAR(20) CHECK (status IN ('PENDING', 'PAID', 'OVERDUE')) DEFAULT 'PENDING',
    FOREIGN KEY (loan_id) REFERENCES loans(id)
);

CREATE TABLE loan_collaterals (
    id UUID PRIMARY KEY,
    loan_id UUID NOT NULL,
    collateral_type VARCHAR(50),
    value DECIMAL(15, 2),
    description TEXT,
    FOREIGN KEY (loan_id) REFERENCES loans(id)
);

CREATE TABLE fixed_deposits (
    id UUID PRIMARY KEY,
    customer_id UUID NOT NULL,
    principal_amount DECIMAL(15, 2) NOT NULL,
    interest_rate DECIMAL(5, 2) NOT NULL,
    maturity_date DATE NOT NULL,
    status VARCHAR(20) CHECK (status IN ('ACTIVE', 'MATURED', 'WITHDRAWN')) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(id)
);

CREATE TABLE debit_cards (
    id UUID PRIMARY KEY,
    account_id UUID NOT NULL,
    card_number VARCHAR(16) UNIQUE NOT NULL,
    expiry_date DATE NOT NULL,
    cvv VARCHAR(3) NOT NULL,
    status VARCHAR(20) CHECK (status IN ('ACTIVE', 'BLOCKED', 'EXPIRED')) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (account_id) REFERENCES accounts(id)
);

CREATE TABLE credit_cards (
    id UUID PRIMARY KEY,
    customer_id UUID NOT NULL,
    card_number VARCHAR(16) UNIQUE NOT NULL,
    credit_limit DECIMAL(15, 2) NOT NULL,
    available_credit DECIMAL(15, 2) NOT NULL,
    expiry_date DATE NOT NULL,
    cvv VARCHAR(3) NOT NULL,
    status VARCHAR(20) CHECK (status IN ('ACTIVE', 'BLOCKED', 'EXPIRED')) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(id)
);

CREATE TABLE credit_card_transactions (
    id UUID PRIMARY KEY,
    credit_card_id UUID NOT NULL,
    amount DECIMAL(15, 2) NOT NULL,
    merchant_name VARCHAR(100),
    transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) CHECK (status IN ('PENDING', 'COMPLETED', 'FAILED')),
    FOREIGN KEY (credit_card_id) REFERENCES credit_cards(id)
);

CREATE TABLE credit_card_bills (
    id UUID PRIMARY KEY,
    credit_card_id UUID NOT NULL,
    billing_cycle_start DATE,
    billing_cycle_end DATE,
    total_amount_due DECIMAL(15, 2) NOT NULL,
    minimum_amount_due DECIMAL(15, 2) NOT NULL,
    due_date DATE NOT NULL,
    status VARCHAR(20) CHECK (status IN ('UNPAID', 'PARTIAL', 'PAID')) DEFAULT 'UNPAID',
    FOREIGN KEY (credit_card_id) REFERENCES credit_cards(id)
);

CREATE TABLE bill_merchants (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    category VARCHAR(50),
    status VARCHAR(20) DEFAULT 'ACTIVE'
);

CREATE TABLE payment_gateway_logs (
    id UUID PRIMARY KEY,
    transaction_id UUID NOT NULL,
    gateway_name VARCHAR(50),
    gateway_reference VARCHAR(100),
    request_payload TEXT,
    response_payload TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (transaction_id) REFERENCES transactions(id)
);

CREATE TABLE audit_logs (
    id UUID PRIMARY KEY,
    entity_name VARCHAR(100),
    entity_id VARCHAR(100),
    action VARCHAR(50),
    performed_by VARCHAR(100),
    old_value TEXT,
    new_value TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE security_audit_logs (
    id UUID PRIMARY KEY,
    username VARCHAR(100),
    action VARCHAR(50),
    ip_address VARCHAR(50),
    status VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE system_configurations (
    config_key VARCHAR(100) PRIMARY KEY,
    config_value VARCHAR(255) NOT NULL,
    description TEXT,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE pending_operations (
    id UUID PRIMARY KEY,
    operation_type VARCHAR(50),
    payload TEXT,
    status VARCHAR(20) CHECK (status IN ('PENDING', 'PROCESSING', 'COMPLETED', 'FAILED')) DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
