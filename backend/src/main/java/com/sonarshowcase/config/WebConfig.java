package com.sonarshowcase.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web configuration with insecure CORS settings
 * 
 * SEC-08: CORS Wildcard - allows any origin
 * 
 * This configuration class sets up CORS (Cross-Origin Resource Sharing) settings
 * for the application. Note: The current configuration is intentionally insecure
 * for demonstration purposes.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Default constructor for Spring configuration.
     * Spring will use this constructor to create the configuration bean.
     */
    public WebConfig() {
        // Default constructor for Spring
    }

    /**
     * CORS configuration restricted to trusted origins.
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("https://trustedwebsite.com")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("*")
                .allowCredentials(true);
    }
    
    /**
     * Alternative CORS filter with same issues
     * 
     * @return CorsFilter instance with permissive CORS configuration
     */
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        
        // SEC: Even more permissive CORS
        config.addAllowedOriginPattern("*");
        config.addAllowedMethod("*");
        config.addAllowedHeader("*");
        config.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        
        return new CorsFilter(source);
    }
}

