ALTER TABLE users ADD COLUMN recovery_hash TEXT;
CREATE INDEX idx_users_recovery_hash ON users(recovery_hash);
