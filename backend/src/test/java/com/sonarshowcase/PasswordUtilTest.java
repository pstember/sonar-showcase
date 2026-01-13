package com.sonarshowcase;

import com.sonarshowcase.util.PasswordUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for PasswordUtil
 */
class PasswordUtilTest {
    
    @Test
    @DisplayName("Should hash password")
    void testHashPassword() {
        String password = "testPassword123";
        String hash = PasswordUtil.hashPassword(password);
        
        assertNotNull(hash);
        assertFalse(hash.isEmpty());
        assertNotEquals(password, hash);
    }
    
    @Test
    @DisplayName("Should hash password consistently")
    void testHashPassword_consistency() {
        String password = "testPassword123";
        String hash1 = PasswordUtil.hashPassword(password);
        String hash2 = PasswordUtil.hashPassword(password);
        
        assertEquals(hash1, hash2);
    }
    
    @Test
    @DisplayName("Should hash different passwords differently")
    void testHashPassword_differentPasswords() {
        String hash1 = PasswordUtil.hashPassword("password1");
        String hash2 = PasswordUtil.hashPassword("password2");
        
        assertNotEquals(hash1, hash2);
    }
    
    @Test
    @DisplayName("Should handle null password")
    void testHashPassword_null() {
        String hash = PasswordUtil.hashPassword(null);
        
        assertNotNull(hash);
    }
    
    @Test
    @DisplayName("Should handle empty password")
    void testHashPassword_empty() {
        String hash = PasswordUtil.hashPassword("");
        
        assertNotNull(hash);
        assertFalse(hash.isEmpty());
    }
    
    @Test
    @DisplayName("Should hash with SHA-1")
    void testHashWithSha1() {
        String input = "testInput";
        String hash = PasswordUtil.hashWithSha1(input);
        
        assertNotNull(hash);
        assertFalse(hash.isEmpty());
    }
    
    @Test
    @DisplayName("Should hash with SHA-1 consistently")
    void testHashWithSha1_consistency() {
        String input = "testInput";
        String hash1 = PasswordUtil.hashWithSha1(input);
        String hash2 = PasswordUtil.hashWithSha1(input);
        
        assertEquals(hash1, hash2);
    }
    
    @Test
    @DisplayName("Should handle null input for SHA-1")
    void testHashWithSha1_null() {
        assertThrows(NullPointerException.class, () -> {
            PasswordUtil.hashWithSha1(null);
        });
    }
    
    @Test
    @DisplayName("Should verify password correctly")
    void testVerifyPassword() {
        String password = "testPassword123";
        String hash = PasswordUtil.hashPassword(password);
        
        boolean result = PasswordUtil.verifyPassword(password, hash);
        
        assertTrue(result);
    }
    
    @Test
    @DisplayName("Should reject incorrect password")
    void testVerifyPassword_incorrect() {
        String password = "testPassword123";
        String hash = PasswordUtil.hashPassword(password);
        
        boolean result = PasswordUtil.verifyPassword("wrongPassword", hash);
        
        assertFalse(result);
    }
    
    @Test
    @DisplayName("Should handle null password in verification")
    void testVerifyPassword_nullPassword() {
        String hash = PasswordUtil.hashPassword("test");
        
        boolean result = PasswordUtil.verifyPassword(null, hash);
        
        assertFalse(result);
    }
    
    @Test
    @DisplayName("Should handle null hash in verification")
    void testVerifyPassword_nullHash() {
        boolean result = PasswordUtil.verifyPassword("test", null);
        
        assertFalse(result);
    }
    
    @Test
    @DisplayName("Should validate password")
    void testIsValidPassword() {
        assertTrue(PasswordUtil.isValidPassword("test"));
        assertTrue(PasswordUtil.isValidPassword("password123"));
        assertTrue(PasswordUtil.isValidPassword("a".repeat(100)));
    }
    
    @Test
    @DisplayName("Should reject null password")
    void testIsValidPassword_null() {
        assertFalse(PasswordUtil.isValidPassword(null));
    }
    
    @Test
    @DisplayName("Should reject short password")
    void testIsValidPassword_short() {
        assertFalse(PasswordUtil.isValidPassword("abc"));
        assertFalse(PasswordUtil.isValidPassword(""));
    }
    
    @Test
    @DisplayName("Should accept password at minimum length")
    void testIsValidPassword_minimumLength() {
        assertTrue(PasswordUtil.isValidPassword("abcd"));
    }
    
    @Test
    @DisplayName("Should handle dangerous password storage")
    void testDangerousPasswordStorage() {
        assertDoesNotThrow(() -> {
            PasswordUtil.dangerousPasswordStorage("testPassword");
        });
    }
    
    @Test
    @DisplayName("Should handle null password in storage")
    void testDangerousPasswordStorage_null() {
        // The method will throw NPE when trying to call .length() on null
        assertThrows(NullPointerException.class, () -> {
            PasswordUtil.dangerousPasswordStorage(null);
        });
    }
}
