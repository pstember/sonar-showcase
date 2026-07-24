package com.sonarshowcase.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

/**
 * Thin admin gate for audit export endpoints: shared secret via {@code X-Admin-Key} header.
 * Demo-grade only; replace with Spring Security for production.
 */
@Component
public class AuditAccessVerifier {

    @Value("${audit.admin-key:}")
    private String expectedKey;

    /**
     * Ensures the provided key matches configured {@code audit.admin-key}.
     * If no key is configured, all requests are rejected.
     *
     * @param providedKey value of X-Admin-Key header (may be null)
     */
    public void requireValidKey(String providedKey) {
        if (expectedKey == null || expectedKey.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Audit admin key is not configured");
        }
        if (providedKey == null || !MessageDigest.isEqual(
                expectedKey.getBytes(StandardCharsets.UTF_8),
                providedKey.getBytes(StandardCharsets.UTF_8))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or missing X-Admin-Key");
        }
    }
}
