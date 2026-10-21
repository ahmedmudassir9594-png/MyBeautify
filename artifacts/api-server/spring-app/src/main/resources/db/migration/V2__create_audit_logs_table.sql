-- Migration: V2 Create audit_logs table
CREATE TABLE IF NOT EXISTS audit_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    actor_id BIGINT NULL,
    actor_email VARCHAR(190) NULL,
    actor_role VARCHAR(50) NULL,
    action VARCHAR(50) NOT NULL,
    resource_type VARCHAR(100) NULL,
    resource_id VARCHAR(100) NULL,
    result VARCHAR(20) NOT NULL DEFAULT 'SUCCESS',
    description VARCHAR(1000) NULL,
    previous_state TEXT NULL,
    new_state TEXT NULL,
    request_method VARCHAR(10) NULL,
    request_endpoint VARCHAR(255) NULL,
    ip_address VARCHAR(64) NULL,
    correlation_id VARCHAR(64) NULL,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_audit_logs_timestamp (timestamp),
    INDEX idx_audit_logs_actor_id (actor_id),
    INDEX idx_audit_logs_action (action),
    INDEX idx_audit_logs_resource (resource_type, resource_id),
    INDEX idx_audit_logs_result (result)
);