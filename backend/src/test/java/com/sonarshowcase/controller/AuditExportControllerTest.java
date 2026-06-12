/*
 * Facts (gate): (1) Invoked only by Maven Surefire; exercises AuditExportController.
 * (2) No prior AuditExportControllerTest in repo. (3) No file I/O; synthetic DTOs only.
 * (4) User: "Implement the plan as specified, it is attached for your reference. Do NOT edit the plan file itself."
 */
package com.sonarshowcase.controller;

import com.sonarshowcase.dto.audit.ActivityLogAuditDto;
import com.sonarshowcase.dto.audit.OrderAuditDto;
import com.sonarshowcase.service.AuditAccessVerifier;
import com.sonarshowcase.service.AuditExportService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuditExportController.class)
@Import(AuditAccessVerifier.class)
@TestPropertySource(properties = "audit.admin-key=test-audit-key")
class AuditExportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuditExportService auditExportService;

    @Test
    void activityLogs_missingAdminKey_returns401() throws Exception {
        mockMvc.perform(get("/api/v1/audit/activity-logs")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void activityLogs_wrongAdminKey_returns401() throws Exception {
        mockMvc.perform(get("/api/v1/audit/activity-logs")
                        .header("X-Admin-Key", "wrong")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void activityLogs_fromAfterTo_returns400() throws Exception {
        mockMvc.perform(get("/api/v1/audit/activity-logs")
                        .header("X-Admin-Key", "test-audit-key")
                        .param("from", "2026-06-02T00:00:00Z")
                        .param("to", "2026-06-01T00:00:00Z")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void activityLogs_ok_returnsPage() throws Exception {
        ActivityLogAuditDto row = new ActivityLogAuditDto(
                1L, 2L, "LOGIN", "ok", Instant.parse("2026-01-01T12:00:00Z"), "127.0.0.1");
        when(auditExportService.findActivityLogs(eq(2L), eq("LOGIN"), any(), any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(row), PageRequest.of(0, 20), 1));

        mockMvc.perform(get("/api/v1/audit/activity-logs")
                        .header("X-Admin-Key", "test-audit-key")
                        .param("userId", "2")
                        .param("action", "LOGIN")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].action").value("LOGIN"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void activityLogs_capsPageSizeAt100() throws Exception {
        when(auditExportService.findActivityLogs(isNull(), isNull(), isNull(), isNull(), any(Pageable.class)))
                .thenAnswer(invocation -> {
                    Pageable p = invocation.getArgument(4);
                    return new PageImpl<ActivityLogAuditDto>(List.of(), p, 0);
                });

        mockMvc.perform(get("/api/v1/audit/activity-logs")
                        .header("X-Admin-Key", "test-audit-key")
                        .param("size", "500")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(auditExportService).findActivityLogs(isNull(), isNull(), isNull(), isNull(),
                org.mockito.ArgumentMatchers.argThat((Pageable p) -> p.getPageSize() == 100));
    }

    @Test
    void orders_ok_returnsPage() throws Exception {
        OrderAuditDto row = new OrderAuditDto(
                1L, "ORD-1", new BigDecimal("10.00"), "PENDING",
                Instant.parse("2026-01-01T00:00:00Z"), 3L);
        when(auditExportService.findOrders(isNull(), eq("PENDING"), isNull(), isNull(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(row), PageRequest.of(0, 10), 1));

        mockMvc.perform(get("/api/v1/audit/orders")
                        .header("X-Admin-Key", "test-audit-key")
                        .param("status", "PENDING")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].orderNumber").value("ORD-1"));
    }
}
