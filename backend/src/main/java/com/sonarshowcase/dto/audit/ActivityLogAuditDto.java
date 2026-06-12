package com.sonarshowcase.dto.audit;

import java.time.Instant;

/**
 * Narrow projection for audit export of activity logs (no internal entity graph).
 */
public record ActivityLogAuditDto(
        Long id,
        Long userId,
        String action,
        String details,
        Instant timestamp,
        String ipAddress
) {
}
