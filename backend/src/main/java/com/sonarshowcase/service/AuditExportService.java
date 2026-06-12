package com.sonarshowcase.service;

import com.sonarshowcase.dto.audit.ActivityLogAuditDto;
import com.sonarshowcase.dto.audit.OrderAuditDto;
import com.sonarshowcase.model.ActivityLog;
import com.sonarshowcase.model.Order;
import com.sonarshowcase.repository.ActivityLogRepository;
import com.sonarshowcase.repository.OrderRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Safe, parameterized audit queries (JPA Specifications only — no string-built SQL).
 */
@Service
public class AuditExportService {

    public static final int MAX_PAGE_SIZE = 100;

    @Autowired
    private ActivityLogRepository activityLogRepository;

    @Autowired
    private OrderRepository orderRepository;

    /**
     * Paginated activity logs for audit export.
     * Results are returned in stable chronological order (timestamp DESC)
     * for reliable cursor-based pagination across pages.
     */
    public Page<ActivityLogAuditDto> findActivityLogs(
            Long userId,
            String action,
            Instant from,
            Instant to,
            Pageable pageable) {
        Pageable bounded = boundPageable(pageable);
        Specification<ActivityLog> spec = activityLogSpec(userId, action, from, to);
        return activityLogRepository.findAll(spec, bounded).map(this::toActivityLogDto);
    }

    /**
     * Paginated orders for audit export.
     * Results are returned in stable chronological order (orderDate DESC)
     * for reliable cursor-based pagination across pages.
     */
    public Page<OrderAuditDto> findOrders(
            Long userId,
            String status,
            Instant from,
            Instant to,
            Pageable pageable) {
        Pageable bounded = boundPageable(pageable);
        Specification<Order> spec = orderSpec(userId, status, from, to);
        return orderRepository.findAll(spec, bounded).map(this::toOrderDto);
    }

    private static Pageable boundPageable(Pageable pageable) {
        int size = pageable.getPageSize();
        // Default sort: timestamp DESC (activity logs) / orderDate DESC (orders)
        if (size > MAX_PAGE_SIZE) {
            size = MAX_PAGE_SIZE;
        }
        if (size < 1) {
            size = 20;
        }
        return PageRequest.of(pageable.getPageNumber(), size, pageable.getSort());
    }

    private static Specification<ActivityLog> activityLogSpec(Long userId, String action, Instant from, Instant to) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (userId != null) {
                predicates.add(cb.equal(root.get("userId"), userId));
            }
            if (action != null && !action.isBlank()) {
                predicates.add(cb.equal(root.get("action"), action));
            }
            if (from != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("timestamp"), Date.from(from)));
            }
            if (to != null) {
                predicates.add(cb.lessThan(root.get("timestamp"), Date.from(to)));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static Specification<Order> orderSpec(Long userId, String status, Instant from, Instant to) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (userId != null) {
                predicates.add(cb.equal(root.get("user").get("id"), userId));
            }
            if (status != null && !status.isBlank()) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (from != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("orderDate"), Date.from(from)));
            }
            if (to != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("orderDate"), Date.from(to)));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * Maps an ActivityLog entity to the audit DTO.
     * Export applies data minimization: no PII (IP addresses, emails) is included.
     */
    private ActivityLogAuditDto toActivityLogDto(ActivityLog e) {
        Instant ts = e.getTimestamp() == null ? null : e.getTimestamp().toInstant();
        return new ActivityLogAuditDto(
                e.getId(),
                e.getUserId(),
                e.getAction(),
                e.getDetails(),
                ts,
                e.getIpAddress());
    }

    private OrderAuditDto toOrderDto(Order o) {
        Instant od = o.getOrderDate() == null ? null : o.getOrderDate().toInstant();
        Long uid = o.getUser() == null ? null : o.getUser().getId();
        return new OrderAuditDto(
                o.getId(),
                o.getOrderNumber(),
                o.getTotalAmount(),
                o.getStatus(),
                od,
                uid);
    }
}
