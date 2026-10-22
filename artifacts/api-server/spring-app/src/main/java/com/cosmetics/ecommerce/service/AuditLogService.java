package com.cosmetics.ecommerce.service;

import com.cosmetics.ecommerce.dto.AuditLogResponse;
import com.cosmetics.ecommerce.dto.PageResponse;
import com.cosmetics.ecommerce.entity.AuditLog;
import com.cosmetics.ecommerce.entity.User;
import com.cosmetics.ecommerce.enums.AuditAction;
import com.cosmetics.ecommerce.enums.AuditResult;
import com.cosmetics.ecommerce.exception.ResourceNotFoundException;
import com.cosmetics.ecommerce.repository.AuditLogRepository;
import jakarta.persistence.criteria.Predicate;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class AuditLogService {

    private static final Logger log = LoggerFactory.getLogger(AuditLogService.class);

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    /**
     * Record an audit event using the current SecurityContext and RequestContext.
     * Uses Propagation.REQUIRES_NEW so the audit log is committed independently of the caller's transaction.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public AuditLog logEvent(AuditAction action, AuditResult result, String resourceType, String resourceId,
                             String description, String previousState, String newState) {
        ActorDetails actor = resolveCurrentActor();
        RequestDetails request = resolveCurrentRequest();

        AuditLog entry = new AuditLog(
                actor.id(),
                actor.email(),
                actor.role(),
                action,
                resourceType,
                resourceId,
                result,
                sanitize(description),
                previousState,
                newState,
                request.method(),
                request.endpoint(),
                request.ipAddress(),
                request.correlationId()
        );

        return persistAuditLog(entry);
    }

    /**
     * Record an audit event with explicitly provided actor details.
     * Useful during authentication flows (login, registration) before or after SecurityContext is populated.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public AuditLog logWithActor(Long actorId, String actorEmail, String actorRole,
                                 AuditAction action, AuditResult result,
                                 String resourceType, String resourceId,
                                 String description, String previousState, String newState) {
        RequestDetails request = resolveCurrentRequest();

        AuditLog entry = new AuditLog(
                actorId,
                sanitizeEmail(actorEmail),
                actorRole,
                action,
                resourceType,
                resourceId,
                result,
                sanitize(description),
                previousState,
                newState,
                request.method(),
                request.endpoint(),
                request.ipAddress(),
                request.correlationId()
        );

        return persistAuditLog(entry);
    }

    /**
     * Record a security failure event safely without exposing credentials or tokens.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public AuditLog logSecurityFailure(AuditAction action, String attemptedEmail, String description) {
        RequestDetails request = resolveCurrentRequest();

        AuditLog entry = new AuditLog(
                null,
                sanitizeEmail(attemptedEmail),
                null,
                action,
                "SECURITY",
                null,
                AuditResult.FAILURE,
                sanitize(description),
                null,
                null,
                request.method(),
                request.endpoint(),
                request.ipAddress(),
                request.correlationId()
        );

        return persistAuditLog(entry);
    }

    /**
     * List audit logs with pagination and multi-field filtering. (Append-only read operation)
     */
    @Transactional(readOnly = true)
    public PageResponse<AuditLogResponse> list(Long actorId, String actorEmail, AuditAction action, String resourceType,
                                               String resourceId, AuditResult result,
                                               Instant startDate, Instant endDate,
                                               String search, Pageable pageable) {
        Specification<AuditLog> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (actorId != null) {
                predicates.add(cb.equal(root.get("actorId"), actorId));
            }
            if (StringUtils.hasText(actorEmail)) {
                predicates.add(cb.equal(cb.lower(root.get("actorEmail")), actorEmail.trim().toLowerCase()));
            }
            if (action != null) {
                predicates.add(cb.equal(root.get("action"), action));
            }
            if (StringUtils.hasText(resourceType)) {
                String rt = resourceType.trim().toLowerCase();
                if ("catalog".equals(rt) || "product".equals(rt)) {
                    predicates.add(cb.or(
                            cb.equal(cb.lower(root.get("resourceType")), "product"),
                            cb.equal(cb.lower(root.get("resourceType")), "catalog")
                    ));
                } else if ("auth".equals(rt) || "security".equals(rt)) {
                    predicates.add(cb.or(
                            cb.equal(cb.lower(root.get("resourceType")), "auth"),
                            cb.equal(cb.lower(root.get("resourceType")), "security"),
                            cb.like(cb.lower(root.get("action").as(String.class)), "login%"),
                            cb.like(cb.lower(root.get("action").as(String.class)), "registration%"),
                            cb.like(cb.lower(root.get("action").as(String.class)), "security%")
                    ));
                } else if ("user".equals(rt) || "user_mgmt".equals(rt)) {
                    predicates.add(cb.or(
                            cb.equal(cb.lower(root.get("resourceType")), "user"),
                            cb.like(cb.lower(root.get("action").as(String.class)), "user%")
                    ));
                } else if ("order".equals(rt)) {
                    predicates.add(cb.or(
                            cb.equal(cb.lower(root.get("resourceType")), "order"),
                            cb.equal(cb.lower(root.get("resourceType")), "payment")
                    ));
                } else {
                    predicates.add(cb.equal(cb.lower(root.get("resourceType")), rt));
                }
            }
            if (StringUtils.hasText(resourceId)) {
                predicates.add(cb.equal(root.get("resourceId"), resourceId.trim()));
            }
            if (result != null) {
                predicates.add(cb.equal(root.get("result"), result));
            }
            if (startDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("timestamp"), startDate));
            }
            if (endDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("timestamp"), endDate));
            }
            if (StringUtils.hasText(search)) {
                String pattern = "%" + search.trim().toLowerCase() + "%";
                Predicate descMatch = cb.like(cb.lower(root.get("description")), pattern);
                Predicate emailMatch = cb.like(cb.lower(root.get("actorEmail")), pattern);
                Predicate resourceMatch = cb.like(cb.lower(root.get("resourceId")), pattern);
                Predicate actionMatch = cb.like(cb.lower(root.get("action").as(String.class)), pattern);
                predicates.add(cb.or(descMatch, emailMatch, resourceMatch, actionMatch));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<AuditLogResponse> page = auditLogRepository.findAll(spec, pageable).map(AuditLogResponse::from);
        return PageResponse.from(page);
    }

    /**
     * Backward-compatible overload for callers without explicit actorEmail.
     */
    @Transactional(readOnly = true)
    public PageResponse<AuditLogResponse> list(Long actorId, AuditAction action, String resourceType,
                                               String resourceId, AuditResult result,
                                               Instant startDate, Instant endDate,
                                               String search, Pageable pageable) {
        return list(actorId, null, action, resourceType, resourceId, result, startDate, endDate, search, pageable);
    }

    /**
     * Retrieve a specific audit log by ID.
     */
    @Transactional(readOnly = true)
    public AuditLogResponse getById(Long id) {
        AuditLog auditLog = auditLogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Audit log not found with ID: " + id));
        return AuditLogResponse.from(auditLog);
    }

    private AuditLog persistAuditLog(AuditLog entry) {
        try {
            return auditLogRepository.save(entry);
        } catch (Exception ex) {
            log.error("CRITICAL: Failed to persist audit log entry [action={}, resource={}/{}]: {}",
                    entry.getAction(), entry.getResourceType(), entry.getResourceId(), ex.getMessage(), ex);
            throw ex;
        }
    }

    private ActorDetails resolveCurrentActor() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            return new ActorDetails(null, null, null);
        }

        Object principal = auth.getPrincipal();
        if (principal instanceof User user) {
            return new ActorDetails(user.getId(), user.getEmail(), user.getRole() != null ? user.getRole().name() : null);
        } else if (principal instanceof org.springframework.security.core.userdetails.UserDetails userDetails) {
            String role = userDetails.getAuthorities().stream()
                    .findFirst()
                    .map(a -> a.getAuthority().replace("ROLE_", ""))
                    .orElse(null);
            return new ActorDetails(null, userDetails.getUsername(), role);
        } else if (principal instanceof String email && !"anonymousUser".equalsIgnoreCase(email)) {
            return new ActorDetails(null, email, null);
        }

        return new ActorDetails(null, null, null);
    }

    private RequestDetails resolveCurrentRequest() {
        try {
            var attributes = RequestContextHolder.getRequestAttributes();
            if (attributes instanceof ServletRequestAttributes servletAttributes) {
                HttpServletRequest request = servletAttributes.getRequest();
                String method = request.getMethod();
                String endpoint = request.getRequestURI();
                String ip = extractClientIp(request);
                String correlationId = request.getHeader("X-Correlation-Id");
                if (!StringUtils.hasText(correlationId)) {
                    correlationId = request.getHeader("X-Request-Id");
                }
                return new RequestDetails(method, endpoint, ip, correlationId);
            }
        } catch (Exception e) {
            log.debug("Unable to resolve current HTTP request: {}", e.getMessage());
        }
        return new RequestDetails(null, null, null, null);
    }

    private String extractClientIp(HttpServletRequest request) {
        String xf = request.getHeader("X-Forwarded-For");
        if (StringUtils.hasText(xf)) {
            return xf.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private String sanitize(String input) {
        if (input == null) return null;
        if (input.length() > 1000) {
            return input.substring(0, 997) + "...";
        }
        return input;
    }

    private String sanitizeEmail(String email) {
        if (!StringUtils.hasText(email)) return null;
        return email.trim().toLowerCase();
    }

    private record ActorDetails(Long id, String email, String role) {}
    private record RequestDetails(String method, String endpoint, String ipAddress, String correlationId) {}
}