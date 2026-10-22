package com.cosmetics.ecommerce.dto;

import com.cosmetics.ecommerce.entity.AuditLog;
import com.cosmetics.ecommerce.enums.AuditAction;
import com.cosmetics.ecommerce.enums.AuditResult;

import java.time.Instant;

public record AuditLogResponse(
        Long id,
        Long actorId,
        String actorEmail,
        String actorRole,
        AuditAction action,
        String resourceType,
        String resourceId,
        AuditResult result,
        String description,
        String previousState,
        String newState,
        String requestMethod,
        String requestEndpoint,
        String ipAddress,
        String correlationId,
        Instant timestamp
) {
    public static AuditLogResponse from(AuditLog log) {
        if (log == null) return null;
        return new AuditLogResponse(
                log.getId(),
                log.getActorId(),
                log.getActorEmail(),
                log.getActorRole(),
                log.getAction(),
                log.getResourceType(),
                log.getResourceId(),
                log.getResult(),
                log.getDescription(),
                log.getPreviousState(),
                log.getNewState(),
                log.getRequestMethod(),
                log.getRequestEndpoint(),
                log.getIpAddress(),
                log.getCorrelationId(),
                log.getTimestamp()
        );
    }

    // Compatibility getters for frontend and API consumer views
    public String getCategory() {
        return resourceType != null ? resourceType : "SYSTEM";
    }

    public String getEntityName() {
        return resourceType != null ? resourceType : "—";
    }

    public String getEntityId() {
        return resourceId;
    }

    public String getStatus() {
        return result != null ? result.name() : "SUCCESS";
    }

    public String getDetails() {
        return description != null ? description : "—";
    }

    public String getOldValue() {
        return previousState;
    }

    public String getNewValue() {
        return newState;
    }
}