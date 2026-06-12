package com.sonarshowcase.controller;

import com.sonarshowcase.dto.audit.ActivityLogAuditDto;
import com.sonarshowcase.dto.audit.OrderAuditDto;
import com.sonarshowcase.service.AuditAccessVerifier;
import com.sonarshowcase.service.AuditExportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;

/**
 * Authenticated audit export API (admin shared secret). Uses parameterized JPA only.
 */
@RestController
@RequestMapping("/api/v1/audit")
@Tag(name = "Audit export", description = "Paginated audit views for activity logs and orders. Requires X-Admin-Key (see audit.admin-key).")
public class AuditExportController {

    private final AuditExportService auditExportService;
    private final AuditAccessVerifier auditAccessVerifier;

    public AuditExportController(AuditExportService auditExportService, AuditAccessVerifier auditAccessVerifier) {
        this.auditExportService = auditExportService;
        this.auditAccessVerifier = auditAccessVerifier;
    }

    @Operation(
            summary = "Export activity logs (paginated)",
            description = "Requires header X-Admin-Key matching server property audit.admin-key. "
                    + "Filters are optional; dates are inclusive in UTC. Max page size: "
                    + AuditExportService.MAX_PAGE_SIZE + "."
    )
    @ApiResponse(responseCode = "200", description = "Page of activity log rows")
    @ApiResponse(responseCode = "400", description = "Invalid date range (from after to)")
    @ApiResponse(responseCode = "401", description = "Missing or invalid X-Admin-Key")
    @GetMapping("/activity-logs")
    public ResponseEntity<Page<ActivityLogAuditDto>> exportActivityLogs(
            @Parameter(description = "Admin shared secret", required = true)
            @RequestHeader(value = "X-Admin-Key", required = false) String adminKey,
            @RequestParam(name = "userId", required = false) Long userId,
            @Parameter(description = "Filter by action name; matched case-insensitively (e.g. 'login' matches 'LOGIN')")
            @RequestParam(name = "action", required = false) String action,
            @RequestParam(name = "from", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam(name = "to", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
            @PageableDefault(size = 20) Pageable pageable) {
        auditAccessVerifier.requireValidKey(adminKey);
        validateRange(from, to);
        Pageable bounded = PageRequest.of(pageable.getPageNumber(),
                Math.min(pageable.getPageSize(), AuditExportService.MAX_PAGE_SIZE), pageable.getSort());
        Page<ActivityLogAuditDto> page = auditExportService.findActivityLogs(userId, action, from, to, bounded);
        return ResponseEntity.ok(page);
    }

    @Operation(
            summary = "Export orders (paginated)",
            description = "Requires header X-Admin-Key matching server property audit.admin-key. "
                    + "Optional filters by userId, status, orderDate range (UTC). Max page size: "
                    + AuditExportService.MAX_PAGE_SIZE + "."
    )
    @ApiResponse(responseCode = "200", description = "Page of order rows")
    @ApiResponse(responseCode = "400", description = "Invalid date range")
    @ApiResponse(responseCode = "401", description = "Missing or invalid X-Admin-Key")
    @GetMapping("/orders")
    public ResponseEntity<Page<OrderAuditDto>> exportOrders(
            @RequestHeader(value = "X-Admin-Key", required = false) String adminKey,
            @RequestParam(name = "userId", required = false) Long userId,
            @Parameter(description = "Filter by order status; matched case-insensitively (e.g. 'pending' matches 'PENDING')")
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "from", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam(name = "to", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
            @PageableDefault(size = 20) Pageable pageable) {
        auditAccessVerifier.requireValidKey(adminKey);
        validateRange(from, to);
        Pageable bounded = PageRequest.of(pageable.getPageNumber(),
                Math.min(pageable.getPageSize(), AuditExportService.MAX_PAGE_SIZE), pageable.getSort());
        Page<OrderAuditDto> page = auditExportService.findOrders(userId, status, from, to, bounded);
        return ResponseEntity.ok(page);
    }

    private static void validateRange(Instant from, Instant to) {
        if (from != null && to != null && from.isAfter(to)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "from must be before or equal to to");
        }
    }
}
