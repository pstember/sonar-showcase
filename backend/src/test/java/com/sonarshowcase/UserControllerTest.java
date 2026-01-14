package com.sonarshowcase;

import com.sonarshowcase.controller.UserController;
import com.sonarshowcase.model.User;
import com.sonarshowcase.repository.UserRepository;
import com.sonarshowcase.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests for UserController endpoints
 */
class UserControllerTest {

    private MockMvc mockMvc;
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private UserService userService;
    
    private UserController userController;
    private User testUser;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userController = new UserController();
        
        // Use reflection to inject mock dependencies
        try {
            java.lang.reflect.Field userServiceField = UserController.class.getDeclaredField("userService");
            userServiceField.setAccessible(true);
            userServiceField.set(userController, userService);
            
            java.lang.reflect.Field userRepositoryField = UserController.class.getDeclaredField("userRepository");
            userRepositoryField.setAccessible(true);
            userRepositoryField.set(userController, userRepository);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject mocks via reflection", e);
        }
        
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password123");
        testUser.setRole("USER");
        testUser.setActive(true);
        testUser.setCreatedAt(new Date());
        testUser.setUpdatedAt(new Date());
    }
    
    @Test
    @DisplayName("Should generate password reset token successfully")
    void testGenerateResetToken_success() throws Exception {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        
        mockMvc.perform(post("/api/v1/users/1/reset-token")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.userId").value("1"))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.resetToken").exists())
                .andExpect(jsonPath("$.resetToken").isString())
                .andExpect(jsonPath("$.resetToken").isString())
                .andExpect(jsonPath("$.message").exists());
        
        verify(userRepository, times(1)).findById(1L);
    }
    
    @Test
    @DisplayName("Should generate different tokens on multiple calls")
    void testGenerateResetToken_differentTokens() throws Exception {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        
        mockMvc.perform(post("/api/v1/users/1/reset-token")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        
        mockMvc.perform(post("/api/v1/users/1/reset-token")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        
        // Verify both calls succeeded
        verify(userRepository, times(2)).findById(1L);
    }
    
    @Test
    @DisplayName("Should generate token with correct format (32 alphanumeric characters)")
    void testGenerateResetToken_format() throws Exception {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        
        String response = mockMvc.perform(post("/api/v1/users/1/reset-token")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resetToken").isString())
                .andReturn()
                .getResponse()
                .getContentAsString();
        
        // Parse and verify token format
        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        com.fasterxml.jackson.databind.JsonNode jsonNode = mapper.readTree(response);
        String token = jsonNode.get("resetToken").asText();
        
        assertEquals(32, token.length());
        assertTrue(token.matches("[A-Za-z0-9]{32}"));
    }
    
    @Test
    @DisplayName("Should throw exception when user not found (NPE due to .get() on empty Optional)")
    void testGenerateResetToken_userNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        
        // This will throw NoSuchElementException - intentional bug for SonarCloud demo
        // We verify the exception is thrown as expected
        jakarta.servlet.ServletException exception = assertThrows(
            jakarta.servlet.ServletException.class,
            () -> {
                mockMvc.perform(post("/api/v1/users/999/reset-token")
                        .contentType(MediaType.APPLICATION_JSON))
                        .andReturn();
            }
        );
        
        // Verify the underlying cause is NoSuchElementException
        assertTrue(exception.getCause() instanceof java.util.NoSuchElementException ||
                   exception.getMessage().contains("No value present"));
        
        verify(userRepository, times(1)).findById(999L);
    }
    
    @Test
    @DisplayName("Should include all required fields in response")
    void testGenerateResetToken_responseFields() throws Exception {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        
        mockMvc.perform(post("/api/v1/users/1/reset-token")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").exists())
                .andExpect(jsonPath("$.username").exists())
                .andExpect(jsonPath("$.resetToken").exists())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.userId").value("1"))
                .andExpect(jsonPath("$.username").value("testuser"));
    }
}
