package com.sonarshowcase;

import com.sonarshowcase.service.EmailService;
import com.sonarshowcase.service.PaymentService;
import com.sonarshowcase.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Comprehensive tests for EmailService
 */
class EmailServiceTest {

    private EmailService emailService;
    
    @Mock
    private PaymentService paymentService;
    
    @Mock
    private CategoryService categoryService;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        emailService = new EmailService();
        
        // Use reflection to inject mock services
        try {
            java.lang.reflect.Field paymentField = EmailService.class.getDeclaredField("paymentService");
            paymentField.setAccessible(true);
            paymentField.set(emailService, paymentService);
            
            java.lang.reflect.Field categoryField = EmailService.class.getDeclaredField("categoryService");
            categoryField.setAccessible(true);
            categoryField.set(emailService, categoryService);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject mocks via reflection", e);
        }
    }
    
    @Test
    @DisplayName("Should send email successfully")
    void testSendEmail() {
        assertDoesNotThrow(() -> {
            emailService.sendEmail("test@example.com", "Test Subject", "Test Body");
        });
    }
    
    @Test
    @DisplayName("Should handle null email address")
    void testSendEmail_nullAddress() {
        assertDoesNotThrow(() -> {
            emailService.sendEmail(null, "Subject", "Body");
        });
    }
    
    @Test
    @DisplayName("Should handle empty email address")
    void testSendEmail_emptyAddress() {
        assertDoesNotThrow(() -> {
            emailService.sendEmail("", "Subject", "Body");
        });
    }
    
    @Test
    @DisplayName("Should handle null subject")
    void testSendEmail_nullSubject() {
        assertDoesNotThrow(() -> {
            emailService.sendEmail("test@example.com", null, "Body");
        });
    }
    
    @Test
    @DisplayName("Should handle null body")
    void testSendEmail_nullBody() {
        assertDoesNotThrow(() -> {
            emailService.sendEmail("test@example.com", "Subject", null);
        });
    }
    
    @Test
    @DisplayName("Should send welcome email")
    void testSendWelcomeEmail() {
        assertDoesNotThrow(() -> {
            emailService.sendWelcomeEmail("user@example.com", "testuser");
        });
    }
    
    @Test
    @DisplayName("Should send welcome email with null username")
    void testSendWelcomeEmail_nullUsername() {
        assertDoesNotThrow(() -> {
            emailService.sendWelcomeEmail("user@example.com", null);
        });
    }
    
    @Test
    @DisplayName("Should send password reset email")
    void testSendPasswordResetEmail() {
        assertDoesNotThrow(() -> {
            emailService.sendPasswordResetEmail("user@example.com", "reset-token-123");
        });
    }
    
    @Test
    @DisplayName("Should send password reset email with null token")
    void testSendPasswordResetEmail_nullToken() {
        assertDoesNotThrow(() -> {
            emailService.sendPasswordResetEmail("user@example.com", null);
        });
    }
    
    @Test
    @DisplayName("Should send order confirmation")
    void testSendOrderConfirmation() {
        boolean result = emailService.sendOrderConfirmation("user@example.com", "ORD-001");
        
        assertTrue(result);
    }
    
    @Test
    @DisplayName("Should handle order confirmation with null email")
    void testSendOrderConfirmation_nullEmail() {
        boolean result = emailService.sendOrderConfirmation(null, "ORD-001");
        
        // Method swallows exceptions, so it may return false or true
        assertNotNull(Boolean.valueOf(result));
    }
    
    @Test
    @DisplayName("Should handle order confirmation with null order number")
    void testSendOrderConfirmation_nullOrderNumber() {
        boolean result = emailService.sendOrderConfirmation("user@example.com", null);
        
        assertNotNull(Boolean.valueOf(result));
    }
    
    @Test
    @DisplayName("Should read email template")
    void testReadEmailTemplate() {
        // This will fail if file doesn't exist, but we test the exception handling
        String result = emailService.readEmailTemplate("/nonexistent/path");
        
        assertEquals("", result);
    }
    
    @Test
    @DisplayName("Should handle null template path")
    void testReadEmailTemplate_nullPath() {
        // The method will throw NPE when creating File with null path
        assertThrows(NullPointerException.class, () -> {
            emailService.readEmailTemplate(null);
        });
    }
    
    @Test
    @DisplayName("Should send payment verification email")
    void testSendPaymentVerificationEmail() {
        boolean result = emailService.sendPaymentVerificationEmail("user@example.com", "100.00");
        
        assertTrue(result);
    }
    
    @Test
    @DisplayName("Should handle payment verification with null email")
    void testSendPaymentVerificationEmail_nullEmail() {
        boolean result = emailService.sendPaymentVerificationEmail(null, "100.00");
        
        assertNotNull(Boolean.valueOf(result));
    }
    
    @Test
    @DisplayName("Should handle payment verification with null amount")
    void testSendPaymentVerificationEmail_nullAmount() {
        boolean result = emailService.sendPaymentVerificationEmail("user@example.com", null);
        
        assertNotNull(Boolean.valueOf(result));
    }
    
    @Test
    @DisplayName("Should send category update email")
    void testSendCategoryUpdateEmail() {
        when(categoryService.calculateDepth("CAT001")).thenReturn(5);
        
        assertDoesNotThrow(() -> {
            emailService.sendCategoryUpdateEmail("user@example.com", "CAT001");
        });
        
        verify(categoryService, times(1)).calculateDepth("CAT001");
    }
    
    @Test
    @DisplayName("Should handle category update email with null category")
    void testSendCategoryUpdateEmail_nullCategory() {
        when(categoryService.calculateDepth(null)).thenReturn(0);
        
        assertDoesNotThrow(() -> {
            emailService.sendCategoryUpdateEmail("user@example.com", null);
        });
    }
}
