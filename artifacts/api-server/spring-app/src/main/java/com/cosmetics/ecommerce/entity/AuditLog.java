package com.cosmetics.ecommerce.entity;

import com.cosmetics.ecommerce.enums.AuditAction;
import com.cosmetics.ecommerce.enums.AuditResult;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(
    name = "audit_logs",
    indexes = {
        @Index(name = "idx_audit_logs_timestamp", columnList = "timestamp"),
        @Index(name = "idx_audit_logs_actor_id", columnList = "actor_id"),
        @Index(name = "idx_audit_logs_action", columnList = "action"),
        @Index(name = "idx_audit_logs_resource", columnList = "resource_type, resource_id"),
        @Index(name = "idx_audit_logs_result", columnList = "result")
    }
)
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "actor_id")
    private Long actorId;

    @Column(name = "actor_email", length = 190)
    private String actorEmail;

    @Column(name = "actor_role", length = 50)
    private String actorRole;

    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false, length = 50)
    private AuditAction action;

    @Column(name = "resource_type", length = 100)
    private String resourceType;

    @Column(name = "resource_id", length = 100)
    private String resourceId;

    @Enumerated(EnumType.STRING)
    @Column(name = "result", nullable = false, length = 20)
    private AuditResult result = AuditResult.SUCCESS;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "previous_state", columnDefinition = "TEXT")
    private String previousState;

    @Column(name = "new_state", columnDefinition = "TEXT")
    private String newState;

    @Column(name = "request_method", length = 10)
    private String requestMethod;

    @Column(name = "request_endpoint", length = 255)
    private String requestEndpoint;

    @Column(name = "ip_address", length = 64)
    private String ipAddress;

    @Column(name = "correlation_id", length = 64)
    private String correlationId;

    @CreationTimestamp
    @Column(name = "timestamp", nullable = false, updatable = false)
    private Instant timestamp;

    protected AuditLog() {}

    public AuditLog(Long actorId, String actorEmail, String actorRole,
                    AuditAction action, String resourceType, String resourceId,
                    AuditResult result, String description,
                    String previousState, String newState,
                    String requestMethod, String requestEndpoint, String ipAddress,
                    String correlationId) {
        this.actorId = actorId;
        this.actorEmail = actorEmail;
        this.actorRole = actorRole;
        this.action = action;
        this.resourceType = resourceType;
        this.resourceId = resourceId;
        this.result = result != null ? result : AuditResult.SUCCESS;
        this.description = description;
        this.previousState = previousState;
        this.newState = newState;
        this.requestMethod = requestMethod;
        this.requestEndpoint = requestEndpoint;
        this.ipAddress = ipAddress;
        this.correlationId = correlationId;
        this.timestamp = Instant.now();
    }

    public Long getId() { return id; }
    public Long getActorId() { return actorId; }
    public String getActorEmail() { return actorEmail; }
    public String getActorRole() { return actorRole; }
    public AuditAction getAction() { return action; }
    public String getResourceType() { return resourceType; }
    public String getResourceId() { return resourceId; }
    public AuditResult getResult() { return result; }
    public String getDescription() { return description; }
    public String getPreviousState() { return previousState; }
    public String getNewState() { return newState; }
    public String getRequestMethod() { return requestMethod; }
    public String getRequestEndpoint() { return requestEndpoint; }
    public String getIpAddress() { return ipAddress; }
    public String getCorrelationId() { return correlationId; }
    public Instant getTimestamp() { return timestamp; }
}