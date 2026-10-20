package com.cosmetics.ecommerce;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminSecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Unauthenticated request to /admin/dashboard returns 401")
    void unauthenticatedRequestToAdminDashboardReturns401() throws Exception {
        mockMvc.perform(get("/admin/dashboard"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Unauthenticated request to /admin/orders returns 401")
    void unauthenticatedRequestToAdminOrdersReturns401() throws Exception {
        mockMvc.perform(get("/admin/orders"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("CUSTOMER role cannot access /admin/dashboard (returns 403)")
    @WithMockUser(username = "customer@example.com", roles = {"CUSTOMER"})
    void customerCannotAccessAdminDashboard() throws Exception {
        mockMvc.perform(get("/admin/dashboard"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("CUSTOMER role cannot access /admin/orders (returns 403)")
    @WithMockUser(username = "customer@example.com", roles = {"CUSTOMER"})
    void customerCannotAccessAdminOrders() throws Exception {
        mockMvc.perform(get("/admin/orders"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("CUSTOMER role cannot access /admin/users (returns 403)")
    @WithMockUser(username = "customer@example.com", roles = {"CUSTOMER"})
    void customerCannotAccessAdminUsers() throws Exception {
        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("STAFF role cannot access /admin/dashboard (returns 403)")
    @WithMockUser(username = "staff@example.com", roles = {"STAFF"})
    void staffCannotAccessAdminDashboard() throws Exception {
        mockMvc.perform(get("/admin/dashboard"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("STAFF role cannot access /admin/users (returns 403)")
    @WithMockUser(username = "staff@example.com", roles = {"STAFF"})
    void staffCannotAccessAdminUsers() throws Exception {
        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("STAFF role can access /admin/orders (returns 200)")
    @WithMockUser(username = "staff@example.com", roles = {"STAFF"})
    void staffCanAccessAdminOrders() throws Exception {
        mockMvc.perform(get("/admin/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("STAFF role can access /admin/inventory (returns 200)")
    @WithMockUser(username = "staff@example.com", roles = {"STAFF"})
    void staffCanAccessAdminInventory() throws Exception {
        mockMvc.perform(get("/admin/inventory"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("ADMIN role can access /admin/dashboard (returns 200)")
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    void adminCanAccessAdminDashboard() throws Exception {
        mockMvc.perform(get("/admin/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("ADMIN role can access /admin/users (returns 200)")
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    void adminCanAccessAdminUsers() throws Exception {
        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
