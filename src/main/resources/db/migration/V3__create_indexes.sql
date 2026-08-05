-- V3__create_indexes.sql

-- Accounts
CREATE INDEX idx_accounts_account_number ON accounts(account_number);
CREATE INDEX idx_accounts_customer_id    ON accounts(customer_id);
CREATE INDEX idx_accounts_status         ON accounts(status);

-- Transactions
CREATE INDEX idx_transactions_account_id        ON transactions(account_id);
CREATE INDEX idx_transactions_linked_account_id ON transactions(linked_account_id);
CREATE INDEX idx_transactions_transaction_date  ON transactions(transaction_date);
CREATE INDEX idx_transactions_status            ON transactions(status);
CREATE INDEX idx_transactions_reference_number  ON transactions(reference_number);
CREATE INDEX idx_transactions_initiated_by      ON transactions(initiated_by);

-- Users
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email    ON users(email);
CREATE INDEX idx_users_status   ON users(status);

-- Refresh tokens
CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens(user_id);
CREATE INDEX idx_refresh_tokens_revoked ON refresh_tokens(revoked);

-- Password reset tokens
CREATE INDEX idx_pwd_reset_tokens_user_id ON password_reset_tokens(user_id);
CREATE INDEX idx_pwd_reset_tokens_used    ON password_reset_tokens(used);

-- Loans
CREATE INDEX idx_loans_customer_id ON loans(customer_id);
CREATE INDEX idx_loans_status      ON loans(status);

-- Loan schedules
CREATE INDEX idx_loan_schedules_loan_id  ON loan_schedules(loan_id);
CREATE INDEX idx_loan_schedules_status   ON loan_schedules(status);
CREATE INDEX idx_loan_schedules_due_date ON loan_schedules(due_date);

-- Credit cards
CREATE INDEX idx_credit_cards_card_number   ON credit_cards(card_number);
CREATE INDEX idx_credit_cards_customer_id   ON credit_cards(customer_id);

-- Debit cards
CREATE INDEX idx_debit_cards_account_id ON debit_cards(account_id);

-- Fixed deposits
CREATE INDEX idx_fixed_deposits_customer_id  ON fixed_deposits(customer_id);
CREATE INDEX idx_fixed_deposits_status       ON fixed_deposits(status);
CREATE INDEX idx_fixed_deposits_maturity     ON fixed_deposits(maturity_date);

-- Audit logs
CREATE INDEX idx_audit_logs_entity_id      ON audit_logs(entity_id);
CREATE INDEX idx_audit_logs_performed_by   ON audit_logs(performed_by);
CREATE INDEX idx_audit_logs_created_at     ON audit_logs(created_at);
CREATE INDEX idx_audit_logs_entity_name    ON audit_logs(entity_name);

-- Security audit logs
CREATE INDEX idx_security_audit_username   ON security_audit_logs(username);
CREATE INDEX idx_security_audit_created_at ON security_audit_logs(created_at);

-- Ledger entries
CREATE INDEX idx_ledger_transaction_id ON ledger_entries(transaction_id);
CREATE INDEX idx_ledger_account_id     ON ledger_entries(account_id);
