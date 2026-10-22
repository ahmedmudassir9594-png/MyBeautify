package com.cosmetics.ecommerce.controller;

import com.cosmetics.ecommerce.dto.ApiResponse;
import com.cosmetics.ecommerce.dto.AuditLogResponse;
import com.cosmetics.ecommerce.dto.PageResponse;
import com.cosmetics.ecommerce.enums.AuditAction;
import com.cosmetics.ecommerce.enums.AuditResult;
import com.cosmetics.ecommerce.service.AuditLogService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/admin/audit-logs")
@PreAuthorize("hasRole('ADMIN')")
public class AuditLogController {

    private final AuditLogService auditLogService;

    public AuditLogController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @GetMapping
    public ApiResponse<PageResponse<AuditLogResponse>> list(
            @RequestParam(required = false) Long actorId,
            @RequestParam(required = false) String actorEmail,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String resourceType,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String entityName,
            @RequestParam(required = false) String resourceId,
            @RequestParam(required = false) String entityId,
            @RequestParam(required = false) AuditResult result,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endDate,
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20, sort = "timestamp", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        String effectiveResourceType = (resourceType != null && !resourceType.isBlank()) ? resourceType
                : ((category != null && !category.isBlank() && !"ALL".equalsIgnoreCase(category)) ? category
                : ((entityName != null && !entityName.isBlank() && !"ALL".equalsIgnoreCase(entityName)) ? entityName : null));

        String effectiveResourceId = (resourceId != null && !resourceId.isBlank()) ? resourceId
                : ((entityId != null && !entityId.isBlank()) ? entityId : null);

        AuditResult effectiveResult = result;
        if (effectiveResult == null && status != null && !status.isBlank() && !"ALL".equalsIgnoreCase(status)) {
            try {
                effectiveResult = AuditResult.valueOf(status.trim().toUpperCase());
            } catch (IllegalArgumentException ignored) {}
        }

        AuditAction effectiveAction = null;
        String effectiveSearch = search;
        if (action != null && !action.isBlank() && !"ALL".equalsIgnoreCase(action)) {
            try {
                effectiveAction = AuditAction.valueOf(action.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                if (effectiveSearch == null || effectiveSearch.isBlank()) {
                    effectiveSearch = action.trim();
                }
            }
        }

        return ApiResponse.ok("Audit logs retrieved successfully.",
                auditLogService.list(actorId, actorEmail, effectiveAction, effectiveResourceType, effectiveResourceId, effectiveResult, startDate, endDate, effectiveSearch, pageable));
    }

    @GetMapping("/{id}")
    public ApiResponse<AuditLogResponse> get(@PathVariable Long id) {
        return ApiResponse.ok("Audit log retrieved successfully.", auditLogService.getById(id));
    }
}