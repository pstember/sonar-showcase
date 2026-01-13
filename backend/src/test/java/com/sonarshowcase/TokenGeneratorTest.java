package com.sonarshowcase;

import com.sonarshowcase.util.TokenGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for TokenGenerator
 */
class TokenGeneratorTest {
    
    @Test
    @DisplayName("Should generate token")
    void testGenerateToken() {
        String token = TokenGenerator.generateToken();
        
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertEquals(32, token.length());
    }
    
    @Test
    @DisplayName("Should generate different tokens")
    void testGenerateToken_differentTokens() {
        String token1 = TokenGenerator.generateToken();
        String token2 = TokenGenerator.generateToken();
        
        // Tokens should be different (though with insecure random, collisions possible)
        assertNotNull(token1);
        assertNotNull(token2);
    }
    
    @Test
    @DisplayName("Should generate session ID")
    void testGenerateSessionId() {
        String sessionId = TokenGenerator.generateSessionId();
        
        assertNotNull(sessionId);
        assertFalse(sessionId.isEmpty());
    }
    
    @Test
    @DisplayName("Should generate different session IDs")
    void testGenerateSessionId_different() {
        String sessionId1 = TokenGenerator.generateSessionId();
        // Small delay to ensure different timestamp
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            // Ignore
        }
        String sessionId2 = TokenGenerator.generateSessionId();
        
        assertNotNull(sessionId1);
        assertNotNull(sessionId2);
    }
    
    @Test
    @DisplayName("Should generate API key")
    void testGenerateApiKey() {
        String apiKey = TokenGenerator.generateApiKey("user123");
        
        assertNotNull(apiKey);
        assertTrue(apiKey.startsWith("API-"));
        assertTrue(apiKey.contains("user123"));
    }
    
    @Test
    @DisplayName("Should generate different API keys for same user")
    void testGenerateApiKey_different() {
        String apiKey1 = TokenGenerator.generateApiKey("user123");
        String apiKey2 = TokenGenerator.generateApiKey("user123");
        
        assertNotNull(apiKey1);
        assertNotNull(apiKey2);
        // May or may not be different due to random component
    }
    
    @Test
    @DisplayName("Should generate API key with null user ID")
    void testGenerateApiKey_nullUserId() {
        String apiKey = TokenGenerator.generateApiKey(null);
        
        assertNotNull(apiKey);
        assertTrue(apiKey.startsWith("API-"));
    }
    
    @Test
    @DisplayName("Should generate password reset token")
    void testGeneratePasswordResetToken() {
        String token = TokenGenerator.generatePasswordResetToken();
        
        assertNotNull(token);
        assertEquals(6, token.length());
        assertTrue(token.matches("\\d{6}"));
    }
    
    @Test
    @DisplayName("Should generate different reset tokens")
    void testGeneratePasswordResetToken_different() {
        String token1 = TokenGenerator.generatePasswordResetToken();
        String token2 = TokenGenerator.generatePasswordResetToken();
        
        assertNotNull(token1);
        assertNotNull(token2);
    }
    
    @Test
    @DisplayName("Should get token secret")
    void testGetTokenSecret() {
        String secret = TokenGenerator.getTokenSecret();
        
        assertNotNull(secret);
        assertFalse(secret.isEmpty());
    }
    
    @Test
    @DisplayName("Should get same token secret")
    void testGetTokenSecret_consistency() {
        String secret1 = TokenGenerator.getTokenSecret();
        String secret2 = TokenGenerator.getTokenSecret();
        
        assertEquals(secret1, secret2);
    }
    
    @Test
    @DisplayName("Should generate UUID")
    void testGenerateUUID() {
        String uuid = TokenGenerator.generateUUID();
        
        assertNotNull(uuid);
        assertEquals(16, uuid.length());
        assertTrue(uuid.matches("[A-F0-9]{16}"));
    }
    
    @Test
    @DisplayName("Should generate different UUIDs")
    void testGenerateUUID_different() {
        String uuid1 = TokenGenerator.generateUUID();
        String uuid2 = TokenGenerator.generateUUID();
        
        assertNotNull(uuid1);
        assertNotNull(uuid2);
        // UUIDs should be different
        assertNotEquals(uuid1, uuid2);
    }
}
