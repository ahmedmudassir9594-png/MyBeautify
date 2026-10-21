package com.cosmetics.ecommerce;

import com.cosmetics.ecommerce.entity.AuditLog;
import com.cosmetics.ecommerce.enums.AuditAction;
import com.cosmetics.ecommerce.enums.AuditResult;
import com.cosmetics.ecommerce.repository.AuditLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuditLogSecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuditLogRepository auditLogRepository;

    private AuditLog sampleLog;

    @BeforeEach
    void setUp() {
        auditLogRepository.deleteAll();
        sampleLog = auditLogRepository.save(new AuditLog(
                1L, "admin@example.com", "ADMIN",
                AuditAction.PRODUCT_CREATE, "PRODUCT", "101",
                AuditResult.SUCCESS, "Product created: Luxury Serum",
                null, "{\"price\":45.00,\"stock\":20}",
                "POST", "/api/products", "127.0.0.1", "REQ-123"
        ));
    }

    @Test
    @DisplayName("Anonymous request to /admin/audit-logs is rejected with 401 Unauthorized")
    void anonymousCannotAccessAuditLogs() throws Exception {
        mockMvc.perform(get("/admin/audit-logs"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("CUSTOMER role is rejected with 403 Forbidden on /admin/audit-logs")
    @WithMockUser(username = "customer@example.com", roles = {"CUSTOMER"})
    void customerCannotAccessAuditLogs() throws Exception {
        mockMvc.perform(get("/admin/audit-logs"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("STAFF role is rejected with 403 Forbidden on /admin/audit-logs")
    @WithMockUser(username = "staff@example.com", roles = {"STAFF"})
    void staffCannotAccessAuditLogs() throws Exception {
        mockMvc.perform(get("/admin/audit-logs"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("ADMIN role can list audit logs with 200 OK and pagination envelope")
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    void adminCanAccessAuditLogsList() throws Exception {
        mockMvc.perform(get("/admin/audit-logs")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.data.content[0].action").value("PRODUCT_CREATE"))
                .andExpect(jsonPath("$.data.content[0].actorEmail").value("admin@example.com"))
                .andExpect(jsonPath("$.data.totalElements").value(greaterThanOrEqualTo(1)));
    }

    @Test
    @DisplayName("ADMIN role can retrieve a specific audit log by ID")
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    void adminCanGetAuditLogById() throws Exception {
        mockMvc.perform(get("/admin/audit-logs/" + sampleLog.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(sampleLog.getId()))
                .andExpect(jsonPath("$.data.resourceType").value("PRODUCT"))
                .andExpect(jsonPath("$.data.resourceId").value("101"))
                .andExpect(jsonPath("$.data.result").value("SUCCESS"));
    }

    @Test
    @DisplayName("ADMIN query filters by action and resourceType correctly")
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    void adminCanFilterAuditLogs() throws Exception {
        // Query matching filter
        mockMvc.perform(get("/admin/audit-logs")
                        .param("action", "PRODUCT_CREATE")
                        .param("resourceType", "PRODUCT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content", hasSize(1)));

        // Query non-matching filter
        mockMvc.perform(get("/admin/audit-logs")
                        .param("action", "ORDER_CANCEL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content", hasSize(0)));
    }

    @Test
    @DisplayName("Audit log payload never contains password or authentication secret fields")
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    void auditLogsDoNotExposeSecrets() throws Exception {
        mockMvc.perform(get("/admin/audit-logs/" + sampleLog.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.password").doesNotExist())
                .andExpect(jsonPath("$.data.token").doesNotExist())
                .andExpect(jsonPath("$.data.secret").doesNotExist());
    }
}