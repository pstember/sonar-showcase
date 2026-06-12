package com.sonarshowcase.dto.audit;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Narrow projection for audit export of orders.
 */
public record OrderAuditDto(
        Long id,
        String orderNumber,
        BigDecimal totalAmount,
        String status,
        Instant orderDate,
        Long userId
) {
}
